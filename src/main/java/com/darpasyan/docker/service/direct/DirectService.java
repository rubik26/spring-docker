package com.darpasyan.docker.service.direct;


import com.darpasyan.docker.model.direct.Direct;

import java.util.List;

public interface DirectService {
    List<Direct> getDirectsByCurrentUser();
    List<Direct> getDirectByUsername(String username);
    Direct createDirect(int receiverId, Direct direct);

}
