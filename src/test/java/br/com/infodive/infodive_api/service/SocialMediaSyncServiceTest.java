package br.com.infodive.infodive_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import br.com.infodive.infodive_api.entity.ConfigBlog;
import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import br.com.infodive.infodive_api.repository.ConfigBlogRepository;
import br.com.infodive.infodive_api.repository.RedeSocialPostRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SocialMediaSyncServiceTest {

    @Mock
    private InstagramApiService instagramApiService;

    @Mock
    private LinkedinApiService linkedinApiService;

    @Mock
    private RedeSocialPostRepository redeSocialPostRepository;

    @Mock
    private ConfigBlogRepository configBlogRepository;

    @InjectMocks
    private SocialMediaSyncService socialMediaSyncService;

    private RedeSocialPost igPost;
    private RedeSocialPost liPost;

    @BeforeEach
    void setUp() {
        igPost = RedeSocialPost.builder()
                .rede(Rede.INSTAGRAM)
                .externalId("ig-101")
                .textoLegenda("Legenda Instagram")
                .imagemUrl("http://ig.com/pic.jpg")
                .permalinkUrl("http://instagram.com/p/101")
                .likesCount(50)
                .commentsCount(5)
                .publicadoEm(LocalDateTime.now())
                .ativo(true)
                .build();

        liPost = RedeSocialPost.builder()
                .rede(Rede.LINKEDIN)
                .externalId("li-202")
                .textoLegenda("Post LinkedIn")
                .imagemUrl("http://li.com/pic.jpg")
                .permalinkUrl("http://linkedin.com/posts/202")
                .likesCount(120)
                .commentsCount(18)
                .publicadoEm(LocalDateTime.now())
                .ativo(true)
                .build();
    }

    @Test
    void syncAll_ShouldFetchAndSavePosts() {
        ConfigBlog config = ConfigBlog.builder()
                .instagramAccessToken("ig-token")
                .instagramAccountId("ig-acc")
                .linkedinAccessToken("li-token")
                .linkedinOrganizationId("li-org")
                .build();

        when(configBlogRepository.findAll()).thenReturn(List.of(config));
        when(instagramApiService.fetchRecentPosts("ig-token", "ig-acc")).thenReturn(List.of(igPost));
        when(linkedinApiService.fetchRecentPosts("li-token", "li-org")).thenReturn(List.of(liPost));

        when(redeSocialPostRepository.findByRedeAndExternalId(Rede.INSTAGRAM, "ig-101")).thenReturn(Optional.empty());
        when(redeSocialPostRepository.findByRedeAndExternalId(Rede.LINKEDIN, "li-202")).thenReturn(Optional.empty());
        when(redeSocialPostRepository.save(any(RedeSocialPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<RedeSocialPost> result = socialMediaSyncService.syncAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(redeSocialPostRepository, times(2)).save(any(RedeSocialPost.class));
    }
}
