package fr.formation.webflix.controllers.admin.video;

import fr.formation.webflix.entities.UserEntity;
import fr.formation.webflix.entities.VideoEntity;
import fr.formation.webflix.services.CategoryService;
import fr.formation.webflix.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.annotation.MultipartConfig;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping("/admin/videos")
public class AdminVideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("videos", videoService.findAll());
        model.addAttribute("page", "video/index");
        return "admin/index";
    }

    @GetMapping("{id}")
    public String one(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model) {
        Optional<VideoEntity> video = videoService.findById(id);
        if (video.isPresent()) {
            model.addAttribute("video", video.get());
            model.addAttribute("page", "video/detail");
            return "admin/index";
        }
        redirectAttributes.addFlashAttribute("error", "Je n'ai pas trouvé la vidéo avec l'id " + id);
        return "redirect:/admin/videos";
    }

    @GetMapping("add")
    public String formGet(VideoEntity videoEntity, Model model) {
        model.addAttribute("video-error", "");
        model.addAttribute("cover-error", "");
        model.addAttribute("cats", categoryService.findAll());
        model.addAttribute("page", "video/add");
        return "admin/index";
    }


    @GetMapping("{id}/delete")
    public String deleteById(@PathVariable("id") Long id, RedirectAttributes redirAttrs,Model model){
       Optional<VideoEntity> video = videoService.findById(id);
        if(video.isPresent()){
            videoService.deleteById(id);
        }else{
            redirAttrs.addFlashAttribute("error","Cette categorie ne peux pas être supprimer");
        }
model.addAttribute("videos",videoService.findAll());


        return "redirect:/admin/videos";
    }




    @PostMapping("add")
    public String formPost(@Valid VideoEntity videoEntity, BindingResult bindingResult,
                           @RequestParam("video-bin") MultipartFile videoBin,
                           @RequestParam("cover-bin") MultipartFile coverBin,
                           Model model) {

        if (bindingResult.hasErrors()) {
            return formGet(videoEntity, model);
        }


//        try {
//            videoBin.transferTo(
//                    new File("/Users/olprog/IdeaProjects/webflix/src/main/resources/static/files/" + videoBin.getOriginalFilename()));
//        } catch (IOException i) {
//            model.addAttribute("video-error", "Une erreur s'est produite lors du transfert.");
//            return formGet(videoEntity, model);
//        }
//
//        try {
//            coverBin.transferTo(
//                    new File("/Users/olprog/IdeaProjects/webflix/src/main/resources/static/files/" + coverBin.getOriginalFilename()));
//        } catch (IOException i) {
//            model.addAttribute("cover-error", "Une erreur s'est produite lors du transfert.");
//            return formGet(videoEntity, model);
//        }

        Path pathV = Paths.get("src/main/resources/static/files/" + videoBin.getOriginalFilename()).toAbsolutePath();
        try{
            videoBin.transferTo(new File(pathV.toFile().toURI()));
        } catch (IOException i) {
            model.addAttribute("video-error","Une erreur c'est produite lors du transfert");
            return formGet(videoEntity, model);
        }


        Path pathC = Paths.get("src/main/resources/static/files/" + coverBin.getOriginalFilename()).toAbsolutePath();
        try{                                     //Users\Hervé\IdeaProjects\webflix\src\main\resources\static\files
            coverBin.transferTo(new File(pathC.toFile().toURI()));
        } catch (IOException i) {
            model.addAttribute("cover-error","Une erreur c'est produite lors du transfert");
            return formGet(videoEntity, model);
        }





        videoEntity.setUrlVideo(videoBin.getOriginalFilename());
        videoEntity.setCover(coverBin.getOriginalFilename());

        videoService.save(videoEntity);
        return "redirect:/admin/videos";
    }
}
