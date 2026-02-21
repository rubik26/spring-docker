package com.darpasyan.docker.service.direct;


import com.darpasyan.docker.model.direct.dto.DirectRequestDto;
import com.darpasyan.docker.model.direct.dto.DirectResponseDto;

import java.util.List;

public interface DirectService {
    List<DirectResponseDto> getDirectsByCurrentUser();
    List<DirectResponseDto> getDirectByUsername(String username);
    DirectResponseDto createDirect(DirectRequestDto directRequestDto);

    DirectResponseDto getDirectById(int id);
}
