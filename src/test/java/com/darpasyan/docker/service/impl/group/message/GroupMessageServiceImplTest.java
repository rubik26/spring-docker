package com.darpasyan.docker.service.impl.group.message;

import com.darpasyan.docker.builders.group.impl.GroupBuilderImpl;
import com.darpasyan.docker.builders.groupMessage.impl.GroupMessageBuilderImpl;
import com.darpasyan.docker.builders.user.impl.UserBuilderImpl;
import com.darpasyan.docker.model.group.Group;
import com.darpasyan.docker.model.group.message.GroupMessage;
import com.darpasyan.docker.model.group.message.dto.GroupMessageRequestDto;
import com.darpasyan.docker.model.group.message.dto.GroupMessageResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.group.GroupRepo;
import com.darpasyan.docker.repo.group.message.GroupMessageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupMessageServiceImplTest {

    @Mock
    private GroupMessageRepo groupMessageRepo;

    @Mock
    private GroupRepo groupRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private GroupMessageServiceImpl groupMessageService;

    @Captor
    private ArgumentCaptor<GroupMessage> groupMessageCaptor;


    private UserBuilderImpl userBuilder;
    private GroupBuilderImpl groupBuilder;
    private GroupMessageBuilderImpl groupMessageBuilder;

    @BeforeEach
    void init(){
        userBuilder = new UserBuilderImpl();
        groupBuilder = new GroupBuilderImpl();
        groupMessageBuilder = new GroupMessageBuilderImpl();
    }

    private void mockSecurity(User user){
        UserPrincipial userPrincipial = new UserPrincipial(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipial);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    record DataAccess(User user, Group group, GroupMessage groupMessage){}

    private DataAccess userNotParticipant(){
        UserBuilderImpl userBuilder = new UserBuilderImpl();
        GroupBuilderImpl groupBuilder = new GroupBuilderImpl();
        GroupMessageBuilderImpl groupMessageBuilder = new GroupMessageBuilderImpl();


        User user = userBuilder.
                setUsername("Test user").
                build();


        mockSecurity(user);

        Group group = groupBuilder.
                setName("Test Group").
                setParticipants(Set.of()).
                build();

        GroupMessage groupMessage1 = groupMessageBuilder.
                setContent("Test message").
                build();

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        return new DataAccess(user, group, groupMessage1);
    }
    @Test
    void testGetGroupMessagesByGroup() {

        User user = userBuilder.
                setUsername("Test user").
                build();

        System.out.println(user.getId());
        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = groupBuilder.
                setName("Test group").
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage1 = groupMessageBuilder.
                setContent("Test content").
                setDateOfSend(dateOfSend).
                setIsEdited(false).
                setSender(user).
                setGroup(group).
                build();
        GroupMessage groupMessage2 = groupMessageBuilder.
                setContent("Test content 2").
                setDateOfSend(dateOfSend).
                setIsEdited(false).
                setSender(user).
                setGroup(group).
                build();

        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "Test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        GroupMessageResponseDto toDto2 = new GroupMessageResponseDto(
                2,
                "Test content 2",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        user.setGroups(Set.of(group));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findGroupMessagesByGroup(group)).thenReturn(List.of(groupMessage1, groupMessage2));

        List<GroupMessageResponseDto> result = groupMessageService.getGroupMessagesByGroup(group.getId());

        assertEquals(List.of(toDto1, toDto2), result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessagesByGroupWhenUserNotParticipant_shouldThrow(){
        DataAccess data = userNotParticipant();

        User user = data.user;
        Group group = data.group;

       RuntimeException runtimeException = assertThrows(RuntimeException.class, () ->
               groupMessageService.getGroupMessagesByUser(group.getId(), user.getUsername()));

       assertEquals("Access denied. You are not in the group", runtimeException.getMessage());

       SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateGroupMessage() {
        User user = userBuilder.
                setUsername("Test User").build();

        mockSecurity(user);

        Group group =
                groupBuilder.setParticipants(Set.of(user)).build();


        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("test content");

       when(userRepo.findById(1)).thenReturn(Optional.of(user));
       when(groupRepo.findById(1)).thenReturn(Optional.of(group));
       when(groupMessageRepo.save(any(GroupMessage.class))).thenAnswer(invocationOnMock -> {
           GroupMessage gm = invocationOnMock.getArgument(0);
           gm.setId(1);

           return gm;
       });

       GroupMessageResponseDto result = groupMessageService.createGroupMessage(group.getId(), fromDto);

       verify(groupMessageRepo).save(groupMessageCaptor.capture());

       GroupMessage capturedMessage = groupMessageCaptor.getValue();

       assertEquals("test content", capturedMessage.getContent());
       assertEquals(group, capturedMessage.getGroup());
       assertEquals(user, capturedMessage.getUser());
       assertFalse(capturedMessage.isEdited());

       assertEquals("test content", result.getContent());
       assertEquals(group.getId(), result.getGroupId());
       assertEquals(user.getId(), result.getSenderId());
       assertFalse(result.isEdited());

       SecurityContextHolder.clearContext();
    }

    @Test
    void testCreateGroupMessageWhenUserNotParticipant_shouldThrow(){
       User user = userBuilder.build();

       mockSecurity(user);

       Group group = groupBuilder.
               setParticipants(Set.of()).
               build();

        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("try to create a message");

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                groupMessageService.createGroupMessage(group.getId(), fromDto));

        assertEquals("Access denied. You are not in the group", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testEditGroupMessage() {
        User user = userBuilder.build();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage1 = groupMessageBuilder.
                                        setSender(user).
                                        setGroup(group).
                                        build();

        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("test content update");
        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage1));
        when(groupMessageRepo.save(any(GroupMessage.class))).thenAnswer(invocationOnMock -> {
            GroupMessage gm = invocationOnMock.getArgument(0);
            gm.setId(1);

            return gm;
        });

        GroupMessageResponseDto result = groupMessageService.editGroupMessage(groupMessage1.getId(), fromDto);

        verify(groupMessageRepo).save(groupMessageCaptor.capture());

        GroupMessage capturedMessage = groupMessageCaptor.getValue();

        assertEquals("test content update", capturedMessage.getContent());
        assertEquals(group, capturedMessage.getGroup());
        assertEquals(user, capturedMessage.getUser());
        assertTrue(capturedMessage.isEdited());

        assertEquals("test content update", result.getContent());
        assertEquals(group.getId(), result.getGroupId());
        assertEquals(user.getId(), result.getSenderId());
        assertTrue(result.isEdited());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testEdtGroupMessageWhenUserNotSenderOfMessage_shouldThrow(){
        User user = userBuilder.build();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage = groupMessageBuilder.
                setSender(new User()).
                setGroup(group).
                build();

        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage));

        GroupMessageRequestDto fromDto = new GroupMessageRequestDto("try to edit");

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                groupMessageService.editGroupMessage(groupMessage.getId(), fromDto));

        assertEquals("Access denied. You are not a sender of this message", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroupMessage() {
        User user = userBuilder.build();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage = groupMessageBuilder.
                setSender(user).
                setGroup(group).
                build();

        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage));

        groupMessageService.deleteGroupMessage(1);
        verify(groupMessageRepo).deleteById(1);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteGroupMessageWhenUserNotSenderOfMessage_shouldThrow(){
        User user = userBuilder.build();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage = groupMessageBuilder.
                setSender(new User()).
                setGroup(group).
                build();

        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                groupMessageService.deleteGroupMessage(groupMessage.getId()));

        assertEquals("Access denied. You are not a sender of this message", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessagesByUser() {

        User user = userBuilder.build();

        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage1 = groupMessageBuilder.
                setContent("test content").
                setDateOfSend(dateOfSend).
                setIsEdited(false).
                setSender(user).
                setGroup(group).
                build();

        GroupMessage groupMessage2 = groupMessageBuilder.
                setContent("test content2").
                setDateOfSend(dateOfSend).
                setIsEdited(false).
                setSender(user).
                setGroup(group).
                build();


        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        GroupMessageResponseDto toDto2 = new GroupMessageResponseDto(
                2,
                "test content2",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );


        user.setGroups(Set.of(group));

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findMessagesByUser(user)).
                thenReturn(List.of(groupMessage1, groupMessage2));

        List<GroupMessageResponseDto> result = groupMessageService.
                getGroupMessagesByUser(group.getId(), user.getUsername());

        assertEquals(List.of(toDto1, toDto2), result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessagesByUserWhenUserNotParticipant_shouldThrow(){
      DataAccess data = userNotParticipant();
      User user = data.user;
      Group group = data.group;

      RuntimeException exception = assertThrows(RuntimeException.class, () ->
              groupMessageService.getGroupMessagesByUser(group.getId(), user.getUsername()));

        assertEquals("Access denied. You are not in the group", exception.getMessage());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessageById() {
        User user = userBuilder.build();

        LocalDateTime dateOfSend = LocalDateTime.now();

        mockSecurity(user);

        Group group = groupBuilder.
                setParticipants(Set.of(user)).
                build();

        GroupMessage groupMessage1 = groupMessageBuilder.
                setContent("test content").
                setDateOfSend(dateOfSend).
                setIsEdited(false).
                setSender(user).
                setGroup(group).
                build();


        GroupMessageResponseDto toDto1 = new GroupMessageResponseDto(
                1,
                "test content",
                dateOfSend,
                false,
                user.getId(),
                group.getId()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(user));
        when(groupRepo.findById(1)).thenReturn(Optional.of(group));
        when(groupMessageRepo.findById(1)).thenReturn(Optional.of(groupMessage1));

        GroupMessageResponseDto result = groupMessageService.getGroupMessageById(group.getId(), groupMessage1.getId());

        assertEquals(toDto1, result);

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetGroupMessageByIdWhenUserNotParticipant_shouldThrow(){
       DataAccess data = userNotParticipant();

       Group group = data.group;
       GroupMessage groupMessage = data.groupMessage;

       RuntimeException exception = assertThrows(RuntimeException.class, () ->
               groupMessageService.getGroupMessageById(group.getId(), groupMessage.getId()));

        assertEquals("Access denied. You are not in the group", exception.getMessage());

        SecurityContextHolder.clearContext();
    }
}