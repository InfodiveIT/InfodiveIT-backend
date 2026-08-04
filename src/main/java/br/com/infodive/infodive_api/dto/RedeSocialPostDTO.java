package br.com.infodive.infodive_api.dto;

import br.com.infodive.infodive_api.entity.RedeSocialPost;
import br.com.infodive.infodive_api.entity.RedeSocialPost.Rede;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedeSocialPostDTO {

    private UUID id;
    private Rede rede;
    private String externalId;
    private String textoLegenda;
    private String imagemUrl;
    private String permalinkUrl;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime publicadoEm;
    private boolean ativo;

    public static RedeSocialPostDTO fromEntity(RedeSocialPost entity) {
        if (entity == null) return null;
        return RedeSocialPostDTO.builder()
                .id(entity.getId())
                .rede(entity.getRede())
                .externalId(entity.getExternalId())
                .textoLegenda(entity.getTextoLegenda())
                .imagemUrl(entity.getImagemUrl())
                .permalinkUrl(entity.getPermalinkUrl())
                .likesCount(entity.getLikesCount() != null ? entity.getLikesCount() : 0)
                .commentsCount(entity.getCommentsCount() != null ? entity.getCommentsCount() : 0)
                .publicadoEm(entity.getPublicadoEm())
                .ativo(entity.isAtivo())
                .build();
    }
}
