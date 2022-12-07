package fr.formation.webflix.controllers.api;

import fr.formation.webflix.entities.VideoEntity;
import fr.formation.webflix.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RestController
public class ApiVideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping( "/api/videos")
//    @CrossOrigin("*")
    public Iterable<VideoEntity> indexVideo() {
        return videoService.findAllByDateDeletedIsNull();
    }

    @PostMapping( value = "/api/videos")
    public VideoEntity addVideo(@RequestBody VideoEntity videoEntity) {
        System.out.println(videoEntity.getCategory());
        return videoService.save(videoEntity);
    }

    @GetMapping(value = "/api/videos/{id}")
    public VideoEntity findOne(@PathVariable Long id) throws Exception {
        return videoService.findByIdAndDateDeletedIsNull(id).orElseThrow(() -> new Exception("vidéo non trouvée"));
    }

    @PutMapping("/api/videos/{id}")
    public VideoEntity update(@PathVariable Long id, @RequestBody VideoEntity videoEntity){
        return videoService.findById(id)
                .map(video -> {
                    video.setName(videoEntity.getName());
                    video.setDuration(videoEntity.getDuration());
                    video.setOriginCountry(videoEntity.getOriginCountry());
                    video.setSynopsis(videoEntity.getSynopsis());
                    video.setUrlVideo(videoEntity.getUrlVideo());
                    video.setCover(videoEntity.getCover());
                    video.setProductYear(videoEntity.getProductYear());
                    video.setDatePublished(videoEntity.getDatePublished());
                    video.setCategory(videoEntity.getCategory());
                    return videoService.save(video);
                })
                .orElseGet(() -> {
                    return videoService.save(videoEntity);
                });
    }

//    @DeleteMapping("/api/videos/{id}")
//    public ResponseEntity delete(@PathVariable Long id){
//        videoService.deleteById(id);
//        String msg = """
//{
//    "message": "Vidéo supprimée"
//}
//""";
//        return ResponseEntity.ok()
//                .header("Content-Type", "application/json; charset=UTF-8")
//                .body(msg);
//    }

    @DeleteMapping("/api/videos/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        String msg = """
{
    "message": "Vidéo supprimée"
}
""";
        ResponseEntity.BodyBuilder r = ResponseEntity.ok()
                .header("Content-Type", "application/json; charset=UTF-8");

        return videoService.findById(id)
                .map(v -> {
                    v.setDateDeleted(Calendar.getInstance());
                    videoService.save(v);
                    return r.body(msg);
                })
                .orElseGet(()->{
                    String msg1 = """
{
    "message": "Cette vidéo n'existe pas"
}
""";
                    return r.body(msg1);
                });

    }
}
