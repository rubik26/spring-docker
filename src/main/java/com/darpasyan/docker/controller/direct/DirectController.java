package com.darpasyan.docker.controller.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.dto.DirectRequestDto;
import com.darpasyan.docker.model.direct.dto.DirectResponseDto;
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
    public List<DirectResponseDto> myDirects(){
        return directService.getDirectsByCurrentUser();
    }

    @GetMapping("directs/{username}")
    public List<DirectResponseDto> getDirectsByUsername(@PathVariable String username){
        return directService.getDirectByUsername(username);
    }

    @PostMapping("users/{recipientId}/createDirect")
    public DirectResponseDto createDirect(@RequestBody DirectRequestDto directRequestDto, @PathVariable int recipientId){
        directRequestDto.setRecipientId(recipientId);
        return directService.createDirect(directRequestDto);
    }
}
