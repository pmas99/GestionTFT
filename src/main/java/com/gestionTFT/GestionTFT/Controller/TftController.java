package com.gestionTFT.GestionTFT.Controller;

import com.gestionTFT.GestionTFT.Services.FileStorageService;
import com.gestionTFT.GestionTFT.entity.*;
import com.gestionTFT.GestionTFT.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class TftController {

    @Autowired
    private TftRepository tftRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ActaRepository actaRepository;
    @Autowired
    private TribunalRepository tribunalRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private TitulacionRepository titulacionRepository;

    @GetMapping("/temas-tft")
    public String verTodosLosTFTsDisponibles(Model model) {
        List<Tft> tftsDisponibles = tftRepository.findByEstado("Disponible");
        List<String> departamentos = uniqueDepartamentos();
        List<String> tutores = uniqueTftTutores(tftsDisponibles);
        List<Titulacion> titulaciones = titulacionRepository.findAll();
        model.addAttribute("titulaciones", titulaciones);
        model.addAttribute("tutores", tutores);
        model.addAttribute("departamentos", departamentos);
        model.addAttribute("tfts", tftsDisponibles);
        return "temas-tft";
    }
    @GetMapping("/nuevo-tft")
    public String showNewTftForm(Model model) {
        model.addAttribute("tutores", userRepository.findAllByRole(User.Role.ROLE_Tutor));
        model.addAttribute("titulaciones", titulacionRepository.findAll());
        return "nuevo-tft";
    }

    @PostMapping("/nuevo-tft")
    public String handleNewTft(@RequestParam("titulo") String titulo, @RequestParam("descripcion") String descripcion,
                               @RequestParam("objetivos") String objetivos, @RequestParam("metodologia") String metodologia,
                               @RequestParam("resultados") String resultados, @RequestParam(required = false) Integer tutorId,
                               @RequestParam("disponibilidad") String disponibilidad, @RequestParam("beca") Boolean beca,
                               @RequestParam("titulacion") Integer titulacionId, Principal principal, Model model) {

        User user = userRepository.findByEmail(principal.getName());
        Titulacion titulacion = titulacionRepository.findById(titulacionId).get();
        if(userCanCreateTFT(user, titulacion)) {
            try {
                Tft tft = new Tft();
                tft.setTitulo(titulo);
                tft.setDescripcion(descripcion);
                tft.setObjetivos(objetivos);
                tft.setMetodologia(metodologia);
                tft.setResultadosEsperados(resultados);
                tft.setDisponibilidad(disponibilidad);
                tft.setBeca(beca);
                tft.setTitulacion(titulacion);
                if (user.getRole().toString().equals("ROLE_Tutor")) {
                    tft.setTutor(user);
                    tft.setEstado("Disponible");
                    tftRepository.save(tft);
                }
                if (user.getRole().toString().equals("ROLE_Alumno")) {
                    tft.setAlumno(user);
                    tft.setEstado("Propuesto");
                    tftRepository.save(tft);
                    Solicitud solicitud = new Solicitud();
                    solicitud.setTft(tft);
                    solicitud.setAlumno(user);
                    Optional<User> optionalTutor = userRepository.findById(tutorId);
                    User tutor = optionalTutor.get();
                    solicitud.setTutor(tutor);
                    solicitud.setTipo(Solicitud.Tipo.valueOf("Propuesta"));
                    solicitud.setDescripcion("Se propone un tema de TFT");
                    solicitud.setFecha(new Date());
                    solicitudRepository.save(solicitud);
                    return "redirect:/solicitudes";
                }
                return "redirect:/mis-trabajos";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/error-page";
            }
        } else {
            model.addAttribute("error", "Ya esta realizando un TFT para esta misma titulación, por favor elija otra");
            return "/error";

        }
    }

    private boolean userCanCreateTFT(User user, Titulacion titulacion) {
        if(user.getRole().toString().equals("ROLE_Tutor")){
            return true;
        }else if(user.getRole().toString().equals("ROLE_Alumno")){
            Tft existeTft = tftRepository.findByAlumnoAndTitulacionAndEstadoNotIn(user, titulacion, Arrays.asList("Disponible", "Propuesto"));
            return existeTft == null;
        }
        return false;
    }

    @GetMapping("/mis-trabajos")
    public String GetMisTrabajos(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        List<Tft> tfts = new ArrayList<>();
        if (user.getRole().toString().equals("ROLE_Alumno")) {
            tfts = tftRepository.findByAlumno(user); //replace with your repository method
            } else if (user.getRole().toString().equals("ROLE_Tutor")) {
                tfts = tftRepository.findByTutor(user); //replace with your repository method
            } else if (user.getRole().toString().equals("ROLE_Secretario")) {
                List<Tribunal> tirbunales = tribunalRepository.findBySecretario(user);
                for (Tribunal tribunal : tirbunales) {
                    List<Tft> tftTribunal = tftRepository.findByTribunal(tribunal);
                    tfts.addAll(tftTribunal);
                }
            } else if (user.getRole().toString().equals("ROLE_PAS")) {
               tfts = tftRepository.findAll();
            }
        List<String> tutores = uniqueTftTutores(tfts);
        List<String> alumnos = uniqueTftAlumnos(tfts);

        model.addAttribute("tfts", tfts);
        model.addAttribute("rol", user.getRole().toString());
        model.addAttribute("tutores", tutores);
        model.addAttribute("alumnos", alumnos);
        return "mis-trabajos";
    }

    @GetMapping("/entregar-tft/{id}")
    public String EntregarTft(Model model, @PathVariable("id") Integer id) {
        Optional<Tft> tftOptional = tftRepository.findById(id);
        Tft tft = tftOptional.get();
        model.addAttribute("tft", tft);
        return "entregar-tft";
    }

    @PostMapping("/subir-memoria/{id}")
    public String subirMemoria(@RequestParam("file") MultipartFile file, @PathVariable("id") Integer id) {
        Optional<Tft> tftOptional = tftRepository.findById(id);
        Tft tft = tftOptional.get();
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Paths.get("C:/Users/pmas2/Escritorio/Almacenamiento de ficheros TFM/Memorias", tft.getAlumno().getNombre() + fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        tft.setMemoria(path.toString());
        tft.setEstado("Entregado");
        tftRepository.save(tft);
        Solicitud solicitud = new Solicitud();
        solicitud.setTipo(Solicitud.Tipo.Validacion);
        solicitud.setTft(tft);
        solicitud.setAlumno(tft.getAlumno());
        solicitud.setTutor(tft.getTutor());
        solicitud.setFecha(new Date());
        solicitud.setDescripcion("El alumno " + tft.getAlumno().getNombre() + " ha entregado la memoria de su TFT, por favor revísela para decidir si la valida o no");
        solicitudRepository.save(solicitud);
        return "redirect:/mis-trabajos";
    }

    @GetMapping("/descargar-memoria/{id}")
    public ResponseEntity<UrlResource> descargarMemoria(@PathVariable("id") Integer id, HttpServletRequest request){
        Optional<Tft> tftOptional = tftRepository.findById(id);
        Tft tft = tftOptional.get();
        if (!tft.getEstado().equals("Entregado")) {
            throw new IllegalStateException("TFT tiene que estar entregado para descargar la memoria");
        }

        String path = tft.getMemoria();

        UrlResource resource = fileStorageService.loadFileAsResource(path);

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ignored) {
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/detalles-tft/{id}")
    public String getTftDetails(@PathVariable("id") Integer id,
                                Model model, Principal principal) {
        Optional<Tft> tftOptional = tftRepository.findById(id);
        Tft tft = tftOptional.get();
        if(canUserViewTft(principal.getName() ,tft)){
            model.addAttribute("tft", tftOptional.get());
            return "detalles-tft";  // retornar la vista que muestra los detalles del tft
        } else {
            return "temas-tft";
        }
    }

    @GetMapping("/evaluar-tft/{id}")
    public String evaluarTft(Model model, Principal principal, @PathVariable("id") Integer tftId) {
        Optional<Tft> optionalTft = tftRepository.findById(tftId);
        Tft tft = optionalTft.get();
        User secretario = userRepository.findByEmail(principal.getName());
        if(tft.getTribunal().getSecretario().equals(secretario)){
            model.addAttribute("tftId", tftId);;
            return "evaluar-tft";
        }
        return "redirect:/tribunales";
    }

    @PostMapping("/calificar-tft/{tftId}")
    public String calificarTft(Model model, Principal principal, @PathVariable("tftId") Integer tftId,
                               @RequestParam("calificacion") Double calificacion,
                               @RequestParam("matricula") Boolean matricula,
                               @RequestParam(value = "comentario", required = false) String comentario,
                               @RequestParam("fechaHora") String fechaHoraString,
                               @RequestParam("file") MultipartFile file) {
        Optional<Tft> optionalTft = tftRepository.findById(tftId);
        Tft tft = optionalTft.get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime fechaHora = LocalDateTime.parse(fechaHoraString, formatter);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path path = Paths.get("C:/Users/pmas2/Escritorio/Almacenamiento de ficheros TFM/Actas", "actaTFT" + fileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Acta acta = new Acta();
        acta.setTft(tft);
        acta.setCalificacion(calificacion);
        acta.setMatricula(matricula);
        if(comentario != null){
            acta.setComentario(comentario);
        }
        acta.setFechaHora(fechaHora);
        acta.setDocumento(path.toString());
        Integer tribunalId = tft.getTribunal().getId();
        tft.setEstado("Calificado");
        tftRepository.save(tft);
        actaRepository.save(acta);
        return "redirect:/detalles-tribunal/" + tribunalId;
    }


    private boolean canUserViewTft(String email, Tft tft){
        User user = userRepository.findByEmail(email);
        if(user.getRole().toString().equals("ROLE_Alumno")){
            return tft.getEstado().equals("Disponible") || tft.getAlumno().equals(user);
        } else if (user.getRole().toString().equals("ROLE_Tutor")){
            return tft.getTutor().equals(user) || !solicitudRepository.findByTutorAndTftAndTipo(user, tft, Solicitud.Tipo.Propuesta).isEmpty();
        } else if (user.getRole().toString().equals("ROLE_PAS")) {
            return true;
        } else if(user.getRole().toString().equals("ROLE_Secretario")){
            return tft.getTribunal().getSecretario().getNombre().equals(user.getNombre());
        }
        return false;
    }

    public List<String> uniqueTftTutores(List<Tft> tfts){
        return tfts.stream()
                .filter(tft -> tft.getTutor() != null)
                .map(tft -> tft.getTutor().getNombre())
                .distinct().collect(Collectors.toList());
    }
    public List<String> uniqueTftAlumnos(List<Tft> tfts){
        return tfts.stream()
                .filter(tft -> tft.getAlumno() != null)
                .map(tft -> tft.getAlumno().getNombre())
                .distinct().collect(Collectors.toList());
    }
    public List<String> uniqueDepartamentos(){
       List<Departamento> departamentos = departamentoRepository.findAll();
        return  departamentos.stream()
                .map(Departamento::getNombre)
                .distinct()
                .toList();
    }
}
