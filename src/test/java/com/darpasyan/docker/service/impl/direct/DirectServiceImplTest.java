package com.darpasyan.docker.service.impl.direct;

import com.darpasyan.docker.builders.direct.impl.DirectBuilderImpl;
import com.darpasyan.docker.builders.user.impl.UserBuilderImpl;
import com.darpasyan.docker.model.direct.Direct;
import com.darpasyan.docker.model.direct.dto.DirectRequestDto;
import com.darpasyan.docker.model.direct.dto.DirectResponseDto;
import com.darpasyan.docker.model.user.User;
import com.darpasyan.docker.model.user.UserPrincipial;
import com.darpasyan.docker.repo.UserRepo;
import com.darpasyan.docker.repo.direct.DirectRepo;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectServiceImplTest {
    private DirectBuilderImpl directBuilder;
    private UserBuilderImpl userBuilder;

    @Mock
    private DirectRepo directRepo;
    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private DirectServiceImpl directService;

    @Captor
    private ArgumentCaptor<Direct> directCaptor;

    @BeforeEach
    void init(){
        directBuilder = new DirectBuilderImpl();
        userBuilder = new UserBuilderImpl();
    }

    private void mockSecurity(User user){
        UserPrincipial userPrincipial = new UserPrincipial(user);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userPrincipial);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getDirectsByCurrentUser() {
        User sender = userBuilder.build();
        User recipient = userBuilder.build();
        User recipient2 = userBuilder.build();

        mockSecurity(sender);

        Direct direct = directBuilder.
                setSender(sender).
                setRecipient(recipient).
                setDateOfStart(LocalDate.now()).
                build();

        Direct direct2 = directBuilder.
                setSender(sender).
                setRecipient(recipient2).
                setDateOfStart(LocalDate.now()).
                build();

        DirectResponseDto toDto = new DirectResponseDto(
                1,
                sender.getId(),
                sender.getUsername(),
                recipient.getId(),
                recipient.getUsername()
        );

        DirectResponseDto toDto2 = new DirectResponseDto(
                2,
                sender.getId(),
                sender.getUsername(),
                recipient2.getId(),
                recipient2.getUsername()
        );

        when(directRepo.myDirects(sender.getId())).thenReturn(List.of(direct, direct2));

        List<DirectResponseDto> result = directService.getDirectsByCurrentUser();

        assertEquals(List.of(toDto, toDto2), result);
    }

    @Test
    void getDirectByUsername() {
        User sender = userBuilder.build();
        User recipient = userBuilder.
                setUsername("Test Username").
                build();

        mockSecurity(sender);

        Direct direct = directBuilder.
                setSender(sender).
                setRecipient(recipient).
                setDateOfStart(LocalDate.now()).
                build();

        DirectResponseDto toDto = new DirectResponseDto(
                1,
                sender.getId(),
                sender.getUsername(),
                recipient.getId(),
                recipient.getUsername()
        );

        when(userRepo.findById(1)).thenReturn(Optional.of(sender));
        when(directRepo.searchDirects(sender, "Test Username")).thenReturn(List.of(direct));

        List<DirectResponseDto> result = directService.getDirectsByUsername("Test Username");

        assertEquals(List.of(toDto), result);
    }

    @Test
    void createDirect() {
        User sender = userBuilder.
                setUsername("User Test").
                build();

        User recipient = userBuilder.build();

        mockSecurity(sender);

        DirectRequestDto fromDto = new DirectRequestDto(recipient.getId());

        when(userRepo.findById(1)).thenReturn(Optional.of(sender));
        when(userRepo.findById(2)).thenReturn(Optional.of(recipient));
        when(directRepo.save(any(Direct.class))).thenAnswer(invocationOnMock -> {
            Direct d = invocationOnMock.getArgument(0);
            d.setId(1);

            return d;
        });

        DirectResponseDto result = directService.createDirect(fromDto);

        verify(directRepo).save(directCaptor.capture());

        Direct capturedDirect = directCaptor.getValue();

        assertEquals(sender, capturedDirect.getSender());
        assertEquals(recipient, capturedDirect.getRecipient());
        assertEquals(LocalDate.now(), capturedDirect.getDateOfStart());

        assertEquals(1, result.getId());
        assertEquals(sender.getId(), result.getSenderId());
        assertEquals(recipient.getId(), result.getRecipientId());
    }

    @Test
    void getDirectById() {
        User sender = userBuilder.build();
        User recipient = userBuilder.build();

        //mockSecurity(sender);

        Direct direct = directBuilder.
                setSender(sender).
                setRecipient(recipient).
                setDateOfStart(LocalDate.now()).
                build();

        DirectResponseDto toDto = new DirectResponseDto(
                1,
                sender.getId(),
                sender.getUsername(),
                recipient.getId(),
                recipient.getUsername()
        );

        when(directRepo.findById(1)).thenReturn(Optional.of(direct));

        DirectResponseDto result = directService.getDirectById(1);

        assertEquals(toDto, result);
    }
}