package com.darpasyan.docker.service.impl.direct.message;

import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.dto.DirectRequestDto;
import com.darpasyan.docker.model.direct.message.DirectMessage;
import com.darpasyan.docker.model.User.User;
import com.darpasyan.docker.model.User.UserPrincipial;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageRequestDto;
import com.darpasyan.docker.model.direct.message.dto.DirectMessageResponseDto;
import com.darpasyan.docker.repo.direct.message.DirectMessageRepo;
import com.darpasyan.docker.repo.direct.DirectRepo;
import com.darpasyan.docker.repo.UserRepo;
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


    private DirectMessageResponseDto toDirectMessageResponseDto(DirectMessage directMessage){
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

        User user = userRepo.findByIdWithRelations(currentUser.getId());

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
                .map(this::toDirectMessageResponseDto).
                toList();

    }

    @Override
    public List<DirectMessageResponseDto> getDirectMessagesByUsername(int directId, String username) {

        getAccessTest(directId);


        User user = userRepo.findByUsername(username);



        return directMessageRepo.getDirectMessagesByUser(user).
                stream().
                map(this::toDirectMessageResponseDto).
                toList();
    }

    @Override
    public DirectMessageResponseDto createDirectMessage(DirectMessageRequestDto directMessageRequestDto) {
        CreateAccessData accessData = createAccess(directMessageRequestDto.getDirectId());

        User user = accessData.user;

        Direct direct = accessData.direct;

        DirectMessage directMessage = new DirectMessage();

        directMessage.setContent(directMessageRequestDto.getContent());
        directMessage.setDateOfSend(LocalDateTime.now());
        directMessage.setEdited(false);
        directMessage.setWatched(false);

        directMessage.setUser(user);
        directMessage.setDirect(direct);

        directMessageRepo.save(directMessage);

        return toDirectMessageResponseDto(directMessage);
    }

    @Override
    public DirectMessageResponseDto editDirectMessage(DirectMessageRequestDto directMessageRequestDto) {
       DirectMessage directMessageForEdit = putAndDeleteAccess(directMessageRequestDto.getId()).message();

        directMessageForEdit.setContent(directMessageRequestDto.getContent());
        directMessageForEdit.setEdited(true);


        directMessageRepo.save(directMessageForEdit);

        return toDirectMessageResponseDto(directMessageForEdit);

    }




    @Override
    public void deleteDirectMessage(DirectMessageRequestDto directMessageRequestDto) {
        AccessData data = putAndDeleteAccess(directMessageRequestDto.getId());

        User user = data.user();
        DirectMessage directMessage = data.message();


        if(user.getId() != directMessage.getUser().getId()){
            throw new RuntimeException("Access denied");
        }

        directMessageRepo.deleteById(directMessageRequestDto.getId());
    }
}
