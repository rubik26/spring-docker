package com.darpasyan.docker.controller;

import com.darpasyan.docker.model.Direct;
import com.darpasyan.docker.service.DirectService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@AllArgsConstructor
@RequestMapping("api/")
public class DirectController {

    private final DirectService directService;

    @GetMapping("directs/{username}")
    public List<Direct> getDirectsByUsername(@PathVariable String username){
        return directService.getDirectByUsername(username);
    }

    @PostMapping("createDirect")
    public Direct createDirect(@RequestBody Direct direct){
        return directService.createDirect(direct);
    }
}
