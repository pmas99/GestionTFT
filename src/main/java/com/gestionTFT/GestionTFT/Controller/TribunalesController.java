package com.gestionTFT.GestionTFT.Controller;

import com.gestionTFT.GestionTFT.Services.FileStorageService;
import com.gestionTFT.GestionTFT.entity.*;
import com.gestionTFT.GestionTFT.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TribunalesController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TribunalRepository tribunalRepository;
    @Autowired
    private ProfesoresRepository profesoresRepository;
    @Autowired
    private TftRepository tftRepository;
    @Autowired
    private TitulacionRepository titulacionRepository;
    @Autowired
    private DepartamentoRepository departamentoRepository;
    @Autowired
    private ActaRepository actaRepository;
    @Autowired
    private FileStorageService fileStorageService;


    @GetMapping("/tribunales")
    public String showTribunales(Model model, Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        List<Tribunal> tribunales = new ArrayList<>();
        if(user.getRole().toString().equals("ROLE_PAS")){
            tribunales = tribunalRepository.findAll();
        }
        else if (user.getRole().toString().equals("ROLE_Secretario")) {
            tribunales = tribunalRepository.findBySecretario(user);
        }
        model.addAttribute("tribunales", tribunales);
        return "tribunales";
    }


    @GetMapping("/formulario-nuevo-tribunal")
    public String formularioNuevoTribunal(Model model, Principal principal) {

        List<Profesores> profesores = profesoresRepository.findAll();
        List<User> secretarios = userRepository.findAllByRole(User.Role.valueOf("ROLE_Secretario"));
        model.addAttribute("profesores", profesores);
        model.addAttribute("secretarios", secretarios);
        return "nuevo-tribunal";
    }

    @PostMapping("/nuevo-tribunal")
    public String nuevoTribunal(Model model, @RequestParam("nombre") String nombre, @RequestParam("fecha_examen") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_examen,
                                @RequestParam("secretario") Integer secretarioId, @RequestParam("vocal") Integer vocalId, @RequestParam("presidente") Integer presidenteId,
                                @RequestParam("suplente") Integer suplenteId) {
        Optional<User> optionalSecretario = userRepository.findById(secretarioId);
        User secretario = optionalSecretario.get();
        Optional<Profesores> optionalVocal = profesoresRepository.findById(vocalId);
        Profesores vocal = optionalVocal.get();
        Optional<Profesores> optionalSuplente = profesoresRepository.findById(suplenteId);
        Profesores suplente = optionalSuplente.get();
        Optional<Profesores> optionalPresidente = profesoresRepository.findById(presidenteId);
        Profesores presidente = optionalPresidente.get();

        Tribunal tribunal = new Tribunal();
        tribunal.setNombre(nombre);
        tribunal.setFechaExamen(fecha_examen);
        tribunal.setSecretario(secretario);
        tribunal.setPresidente(presidente);
        tribunal.setVocal(vocal);
        tribunal.setSuplente(suplente);
        tribunalRepository.save(tribunal);

        List<Tribunal> tribunales = tribunalRepository.findAll();
        model.addAttribute("tribunales", tribunales);
        return "redirect:/tribunales";
    }

    @GetMapping("/tfts-tribunales")
    public String tftsTribunales(Model model, Principal principal) {
        List<Tribunal> tribunales = tribunalRepository.findAll();
        model.addAttribute("tribunales", tribunales);
        List<Tft> tfts = tftRepository.findByEstado("Validado");
        List<Titulacion> titulaciones = titulacionRepository.findAll();
        List<Departamento> departamentos = departamentoRepository.findAll();
        model.addAttribute("tfts", tfts);
        model.addAttribute("titulaciones", titulaciones);
        model.addAttribute("departamentos", departamentos);
        return "tribunales-tft";
    }

    @PostMapping("/asignar-tribunal")
    public String asignarTribunal(Model model, Principal principal, @RequestParam("tftSelect") List<Integer> tftIds,
                                  @RequestParam("tribunalesSelect") Integer tribunalId) {
        Optional<Tribunal> optionalTribunal = tribunalRepository.findById(tribunalId);
        Tribunal tribunal = optionalTribunal.get();
        for(Integer tftId : tftIds){
            Optional<Tft> optionalTft = tftRepository.findById(tftId);
            Tft tft = optionalTft.get();
            tft.setEstado("Tribunal");
            tft.setTribunal(tribunal);
            tftRepository.save(tft);
        }

      return "redirect:/tfts-tribunales";
    }

    @GetMapping("/detalles-tribunal/{id}")
    public String getTftDetails(@PathVariable("id") Integer tribunalId,
                                Model model, Principal principal) {
        Optional<Tribunal> tribunalOptional = tribunalRepository.findById(tribunalId);
        Tribunal tribunal = tribunalOptional.get();
        if(canUserViewTribunal(principal.getName() ,tribunal)){
            List<Tft> tfts = tftRepository.findByTribunal(tribunal);
            model.addAttribute("tribunal", tribunal);
            model.addAttribute("tfts", tfts);
            return "detalles-tribunal";  // retornar la vista que muestra los detalles del tft
        } else {
            return "tribunales";
        }
    }

    @GetMapping("/descargar-acta/{id}")
    public ResponseEntity<UrlResource> descargarMemoria(@PathVariable("id") Integer id, HttpServletRequest request){
        Optional<Tft> tftOptional = tftRepository.findById(id);
        Tft tft = tftOptional.get();
        if (!tft.getEstado().equals("Calificado")) {
            throw new IllegalStateException("TFT tiene haber sido calificado para descargar la memoria");
        }
        Acta acta = actaRepository.findByTft(tft);
        String path = acta.getDocumento();

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

    private boolean canUserViewTribunal(String name, Tribunal tribunal) {
        User user = userRepository.findByEmail(name);
        if(user.getRole().toString().equals("ROLE_Secretario") && tribunal.getSecretario().getId() == user.getId()){
            return true;
        } else return user.getRole().toString().equals("ROLE_PAS");
    }
}
