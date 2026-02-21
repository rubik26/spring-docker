package com.darpasyan.docker.service.impl.direct.message;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageRequestDto;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.direct.DirectRepo;
import com.darpasyan.docker.repo.direct.message.DirectMessageRepo;
import com.darpasyan.docker.service.direct.message.DirectMessageService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DirectMessageServiceImpl implements DirectMessageService {

    private final DirectMessageRepo directMessageRepo;

    private final UserRepo userRepo;

    private final DirectRepo directRepo;


    private DirectMessageResponseDto toDto(DirectMessage directMessage){
        return new DirectMessageResponseDto(
                directMessage.getId(),
                directMessage.getContent(),
                directMessage.getDateOfSend(),
                directMessage.isEdited(),
                directMessage.isWatched(),
                directMessage.getUser().getId(),
                directMessage.getUser().getUsername()
        );
    }



    private Direct getAccessTest(int directId){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Direct direct = directRepo.findById(directId).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );

        if(direct.getSender().getId() != user.getId() && direct.getRecipient().getId() != user.getId()){
            throw new RuntimeException("Access denied");
        }

        return direct;
    }





    private record AccessData(
            User user,
            DirectMessage message
    ){}

    private record CreateAccessData(
            User user,
            Direct direct
    ){}

    private CreateAccessData createAccess(int directId){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        Direct direct = directRepo.findById(directId).orElseThrow(
                () -> new RuntimeException("Direct not found")
        );

        if(direct.getSender().getId() != user.getId() && direct.getRecipient().getId() != user.getId()){
            throw new RuntimeException("Access denied");
        }

        return new CreateAccessData(user, direct);
    }

    private AccessData putAndDeleteAccess(int id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserPrincipial currentUser =
                (UserPrincipial) authentication.getPrincipal();

        User user = userRepo.findById(currentUser.getId()).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        DirectMessage directMessage = directMessageRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Message not found")
        );



        if(user.getId() != directMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }


        return new AccessData(user, directMessage);
    }




    @Override
    public List<DirectMessageResponseDto> getMessagesByDirect(int directId) {

        return directMessageRepo.getDirectMessagesByDirect(getAccessTest(directId))
                .stream()
                .map(this::toDto).
                toList();

    }

    @Override
    public List<DirectMessageResponseDto> getDirectMessagesByUsername(int directId, String username) {

        getAccessTest(directId);


        User user = userRepo.findByUsername(username);



        return directMessageRepo.getDirectMessagesByUser(user).
                stream().
                map(this::toDto).
                toList();
    }

    @Override
    public DirectMessageResponseDto createDirectMessage(DirectMessageRequestDto fromDto, int directId) {
        CreateAccessData accessData = createAccess(directId);

        User user = accessData.user;

        Direct direct = accessData.direct;

        DirectMessage directMessage = new DirectMessage();

        directMessage.setContent(fromDto.getContent());
        directMessage.setDateOfSend(LocalDateTime.now());
        directMessage.setEdited(false);
        directMessage.setWatched(false);

        directMessage.setUser(user);
        directMessage.setDirect(direct);

        directMessageRepo.save(directMessage);

        return toDto(directMessage);
    }

    @Override
    public DirectMessageResponseDto editDirectMessage(DirectMessageRequestDto fromDto, int id) {
       DirectMessage directMessageForEdit = putAndDeleteAccess(id).message;

        directMessageForEdit.setContent(fromDto.getContent());
        directMessageForEdit.setEdited(true);


        directMessageRepo.save(directMessageForEdit);

        return toDto(directMessageForEdit);

    }




    @Override
    public void deleteDirectMessage(int id) {
        AccessData data = putAndDeleteAccess(id);

        User user = data.user();
        DirectMessage directMessage = data.message();


        if(user.getId() != directMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        directMessageRepo.deleteById(id);
    }

    @Override
    public DirectMessageResponseDto getDirectMessageById(int directId, int id) {
        getAccessTest(directId);

        DirectMessage message = directMessageRepo.findById(id).orElseThrow(
                () -> new RuntimeException("Message not found")
        );

        return toDto(message);
    }
}
