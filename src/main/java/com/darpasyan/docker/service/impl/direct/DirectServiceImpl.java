package com.darpasyan.docker.service.impl.direct;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.dto.DirectRequestDto;
import com.darpasyan.docker.model.direct.dto.DirectResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.direct.DirectRepo;
import com.darpasyan.docker.service.direct.DirectService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectServiceImpl implements DirectService {

    private final DirectRepo directRepo;
    private final UserRepo userRepo;

    private DirectResponseDto toDto(Direct direct){
        return new DirectResponseDto(
                direct.getId(),
                direct.getSender().getId(),
                direct.getSender().getUsername(),
                direct.getRecipient().getId(),
                direct.getRecipient().getUsername()
        );
    }

    @Override
    public List<DirectResponseDto> getDirectsByCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        return directRepo.myDirects(currentUser.getId())
                .stream()
                .map(this::toDto).
                toList();
    }

    @Override
    public List<DirectResponseDto> getDirectsByUsername(String username) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();


        User me = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

       return directRepo.searchDirects(me, username).
               stream().
               map(this::toDto).
               toList();
    }

    @Override
    public DirectResponseDto createDirect(DirectRequestDto directRequestDto) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User me = userRepo.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Your account not found"));

        User receiver = userRepo.findById(directRequestDto.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Direct existing = directRepo.findBetweenUsers(me, receiver);

        if (existing != null) {
            return toDto(existing);
        }

        Direct direct = new Direct();
        direct.setSender(me);
        direct.setRecipient(receiver);
        direct.setDateOfStart(LocalDate.now());

        directRepo.save(direct);

        return toDto(direct);
    }

    @Override
    public DirectResponseDto getDirectById(int id) {

        Direct direct = directRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );

        return toDto(direct);
    }
}
