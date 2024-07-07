package com.gestionTFT.GestionTFT.Controller;

import com.gestionTFT.GestionTFT.entity.Tft;
import com.gestionTFT.GestionTFT.entity.Tribunal;
import com.gestionTFT.GestionTFT.entity.User;
import com.gestionTFT.GestionTFT.repository.TftRepository;
import com.gestionTFT.GestionTFT.repository.TribunalRepository;
import com.gestionTFT.GestionTFT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
@Controller
public class HomeController {

    private final TftRepository tftRepository;
    private final UserRepository userRepository;
    private final TribunalRepository tribunalRepository;

    @Autowired
    public HomeController(TftRepository tftRepository, UserRepository userRepository, TribunalRepository tribunalRepository) {
        this.tftRepository = tftRepository;
        this.userRepository = userRepository;
        this.tribunalRepository = tribunalRepository;
    }

    @GetMapping("/home")
    public String home(Principal principal, Model model) {

        String username = principal.getName();
        User user = userRepository.findByEmail(username);
        String nombre = user.getNombre();
        List<Tft> tfts = new ArrayList<>();
        if(user.getRole().toString().equals("ROLE_Tutor")){
            tfts = tftRepository.findByTutor(user);

        } else if (user.getRole().toString().equals("ROLE_Alumno")) {
            tfts = tftRepository.findByAlumno(user);
        } else if (user.getRole().toString().equals("ROLE_PAS")) {
            tfts = tftRepository.findAll();
        } else if (user.getRole().toString().equals("ROLE_Secretario")) {
            List<Tribunal> tirbunales = tribunalRepository.findBySecretario(user);
            for (Tribunal tribunal : tirbunales) {
                List<Tft> tftTribunal = tftRepository.findByTribunal(tribunal);
                tfts.addAll(tftTribunal);
            }
        }
        String role = user.getRole().toString().substring(5);

        model.addAttribute("role", role);
        model.addAttribute("name", nombre);
        model.addAttribute("tfts", tfts);
        model.addAttribute("rol", user.getRole().toString());

        return "home";
    }
}
