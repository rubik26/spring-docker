package com.darpasyan.docker.controller.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.service.direct.DirectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class DirectController {

    private final DirectService directService;

    @GetMapping("myDirects")
    public List<Direct> myDirects(){
        return directService.getDirectsByCurrentUser();
    }

    @GetMapping("directs/{username}")
    public List<Direct> getDirectsByUsername(@PathVariable String username){
        return directService.getDirectByUsername(username);
    }

    @PostMapping("users/{receiverId}/createDirect")
    public Direct createDirect(@PathVariable int receiverId, @RequestBody Direct direct){
        return directService.createDirect(receiverId, direct);
    }
}
