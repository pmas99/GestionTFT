package com.gestionTFT.GestionTFT.Controller;

import com.gestionTFT.GestionTFT.Services.FileStorageService;
import com.gestionTFT.GestionTFT.entity.Solicitud;
import com.gestionTFT.GestionTFT.entity.Tft;
import com.gestionTFT.GestionTFT.entity.Titulacion;
import com.gestionTFT.GestionTFT.entity.User;
import com.gestionTFT.GestionTFT.repository.SolicitudRepository;
import com.gestionTFT.GestionTFT.repository.TftRepository;
import com.gestionTFT.GestionTFT.repository.TitulacionRepository;
import com.gestionTFT.GestionTFT.repository.UserRepository;
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
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;

@Controller
public class SolicitudesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private TftRepository tftRepository;

    @Autowired
    private TitulacionRepository titulacionRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/solicitudes")
    public String getSolicitudesTutor(Principal principal, Model model, @RequestParam(required = false) String tft,
                                      @RequestParam(required = false) String tipo) {
        User user = userRepository.findByEmail(principal.getName());
        List<Solicitud> solicitudes = new ArrayList<>();
        List<Titulacion> titulaciones = titulacionRepository.findAll();
        if(user.getRole().toString().equals("ROLE_Alumno")) {
             solicitudes = solicitudRepository.findByAlumno(user);
        }
        if(user.getRole().toString().equals("ROLE_Tutor")) {
            solicitudes = solicitudRepository.findByTutor(user);
        }
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("titulaciones", titulaciones);
        return "solicitudes";
    }
    @GetMapping("/nueva-solicitud/{id}")
    public String getSolicitudForm(@PathVariable Integer id, Model model) {
        Optional<Tft> optionalTft = tftRepository.findById(id);
        Tft tft = optionalTft.get();
        model.addAttribute("tft", tft);

        return "nueva-solicitud";
    }

    @PostMapping("/nueva-solicitud/{id}")
    public String handleSolicitudRequest(@RequestParam("description") String description,
                                         @RequestParam("file") MultipartFile file,
                                         Principal principal, @PathVariable Integer id, Model model){
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Optional<Tft> optionalTft = tftRepository.findById(id);
        Tft tft = optionalTft.get();
        User alumno = userRepository.findByEmail(principal.getName());
        Titulacion titulacion = tft.getTitulacion();
        if(userCanRequest(alumno, titulacion)){
            try {
                // Crear una nueva solicitud
                Solicitud solicitud = new Solicitud();
                solicitud.setTipo(Solicitud.Tipo.Solicitud);
                solicitud.setTft(tft);
                solicitud.setFecha(new Date());
                solicitud.setTutor(tft.getTutor());
                solicitud.setAlumno(alumno);
                solicitud.setDescripcion(description);
                // Guardar la solicitud
                solicitudRepository.save(solicitud);

                Path path = Paths.get("C:/Users/pmas2/Escritorio/Almacenamiento de ficheros TFM/CV", solicitud.getAlumno().getNombre() + fileName);
                try {
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                solicitud.setCv(path.toString());
                solicitudRepository.save(solicitud);
                return "redirect:/temas-tft";
            } catch (Exception e) {
                e.printStackTrace();
                return "redirect:/error-page";
            }
        }
        model.addAttribute("error", "Ya esta realizando un TFT para esta misma titulación");
        return "error";
    }
    @GetMapping("/detalles-solicitud/{id}")
    public String getTftDetails(@PathVariable("id") Integer id, Model model, Principal principal) {
        Optional<Solicitud> solicitudOptional = solicitudRepository.findById(id);
        Solicitud solicitud = solicitudOptional.get();
        User user = userRepository.findByEmail(principal.getName());
        if(canUserViewSolicitud(principal.getName() ,solicitud)){
            model.addAttribute("solicitud", solicitudOptional.get());
            model.addAttribute("rol", user.getRole().toString());
            return "detalles-solicitud";
        } else {
            return "solicitudes";
        }
    }

    @PostMapping("/solicitudAceptada")
    @Transactional
    public String aceptarSolicitud(@RequestParam("solicitudId") Integer solicitudId) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(solicitudId);
        Solicitud solicitud = optionalSolicitud.get();
        String tipo = solicitud.getTipo();
        switch (tipo) {
            case "Solicitud" -> {
                //Si es una solicitud para alguno de los temas publicados por el tutor, al aceptarla se eliminan todas las demás
                //y se asigna el TFT al alumno en cuestión, además se anulan el resto de solicitudes vinculadas a ese alumno y esa titulación
                Tft tft = solicitud.getTft();
                User alumno = solicitud.getAlumno();
                Titulacion titulacion = tft.getTitulacion();
                List<Solicitud> solicitudesEliminar = solicitudRepository.findByAlumno(alumno);
                solicitudRepository.deleteAllByTft(tft);
                for(Solicitud s : solicitudesEliminar) {
                    if(s.getTft().getTitulacion().equals(titulacion)){
                        if(s.getTipo().equals("Propuesta")){
                            tftRepository.delete(s.getTft());
                            solicitudRepository.delete(s);
                        } else if (s.getTipo().equals("Solicitud")) {
                            solicitudRepository.delete(s);
                        }
                    }
                }
                tft.setEstado("En elaboracion");
                tft.setAlumno(alumno);
                tftRepository.save(tft);
            }
            case "Propuesta" -> {
                Tft tft = solicitud.getTft();
                User alumno = solicitud.getAlumno();
                User tutor = solicitud.getTutor();
                tft.setAlumno(alumno);
                tft.setTutor(tutor);
                tft.setEstado("En elaboracion");
                solicitudRepository.deleteAllByTft(tft);
            }
            case "Validacion" -> {
                Tft tft = solicitud.getTft();
                tft.setEstado("Validado");
                tftRepository.save(tft);
                solicitudRepository.deleteAllByTft(tft);
                Solicitud newSolicitud = new Solicitud();
                newSolicitud.setDescripcion("El tft "+ tft.getTitulo()+" ha sido validado y debe ser inscrito en un tribunal");
                newSolicitud.setFecha(new Date());
                newSolicitud.setTipo(Solicitud.Tipo.Tribunal);
                newSolicitud.setTft(tft);
                newSolicitud.setAlumno(tft.getAlumno());
                newSolicitud.setTutor(tft.getTutor());
                solicitudRepository.save(newSolicitud);
            }

        }
        return "redirect:/solicitudes";
    }

    @PostMapping("/solicitudCancelada")
    public String cancelarSolicitud(@RequestParam("solicitudId") Integer solicitudId) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(solicitudId);
        Solicitud solicitud = optionalSolicitud.get();
        Tft tft = solicitud.getTft();
        if(solicitud.getTipo().equals("Propuesta")){
            tftRepository.delete(solicitud.getTft());
            solicitudRepository.delete(solicitud);
        } else if(solicitud.getTipo().equals("Solicitud")){
            solicitudRepository.delete(solicitud);
        } else if(solicitud.getTipo().equals("Validacion")){
            tft.setMemoria(null);
            tft.setEstado("En elaboracion");
            solicitudRepository.delete(solicitud);
            tftRepository.save(tft);
        }
        return "redirect:/solicitudes";
    }
    @GetMapping("/descargarCV/{id}")
    public ResponseEntity<UrlResource> descargarMemoria(@PathVariable("id") Integer id, HttpServletRequest request){
        Optional<Solicitud> solicitudOptional = solicitudRepository.findById(id);
        Solicitud solicitud = solicitudOptional.get();

        String path = solicitud.getCv();

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

    private boolean canUserViewSolicitud(String email, Solicitud solicitud){
        User user = userRepository.findByEmail(email);
        if(user.getRole().toString().equals("ROLE_Alumno")){
            return solicitud.getAlumno().equals(user);
        } else if (user.getRole().toString().equals("ROLE_Tutor")){
            return solicitud.getTutor().equals(user);
        }
        else {
            return false;
        }
    }

    public boolean userCanRequest(User user, Titulacion titulacion){
        if(user.getRole().toString().equals("ROLE_Alumno")){
            Tft existeTft = tftRepository.findByAlumnoAndTitulacionAndEstadoNotIn(user, titulacion, Arrays.asList("Disponible", "Propuesto"));
            return existeTft == null;
        }
        return false;
    }
}
