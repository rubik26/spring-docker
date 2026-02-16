package com.darpasyan.docker.service;


import com.darpasyan.docker.model.Direct;

import java.util.List;

public interface DirectService {
    List<Direct> getDirectByUsername(String username);
    Direct createDirect(Direct direct);

}
