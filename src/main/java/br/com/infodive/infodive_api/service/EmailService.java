package br.com.infodive.infodive_api.service;

import br.com.infodive.infodive_api.entity.Lead;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${lead.notification.email:contato@infodive.com.br}")
    private String recipientEmail;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Async
    public void enviarNotificacaoNovoLead(Lead lead) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String sender = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail : "noreply@infodive.com.br";
            helper.setFrom(sender);
            if (lead.getEmail() != null && !lead.getEmail().isBlank()) {
                helper.setReplyTo(lead.getEmail());
            }
            helper.setTo(recipientEmail);
            helper.setSubject("Novo Lead Recebido Através do Site Infodive: " + lead.getNomeCompleto() + " (" + lead.getEmpresa() + ")");

            String produtoNome = lead.getProdutoInteresse() != null ? lead.getProdutoInteresse().getNome() : "Não especificado";
            String dataHora = lead.getCriadoEm() != null ? lead.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";

            String htmlBody = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
                    <div style="background-color: #0f172a; color: #ffffff; padding: 20px; text-align: center;">
                        <h2 style="margin: 0;">Infodive IT — Novo Lead Capturado</h2>
                    </div>
                    <div style="padding: 24px; color: #334155;">
                        <p style="font-size: 16px;">Você recebeu uma nova mensagem de contato através do site:</p>
                        <table style="width: 100%%; border-collapse: collapse; margin-top: 16px;">
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Nome Completo:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">E-mail:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;"><a href="mailto:%s">%s</a></td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Telefone:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Empresa:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Cargo:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Produto de Interesse:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                            <tr><td style="padding: 8px; font-weight: bold; border-bottom: 1px solid #f1f5f9;">Data de Envio:</td><td style="padding: 8px; border-bottom: 1px solid #f1f5f9;">%s</td></tr>
                        </table>
                        <div style="margin-top: 20px; background-color: #f8fafc; padding: 16px; border-radius: 6px; border-left: 4px solid #2563eb;">
                            <strong style="display: block; margin-bottom: 8px;">Mensagem Enviada:</strong>
                            <p style="margin: 0; white-space: pre-wrap;">%s</p>
                        </div>
                    </div>
                    <div style="background-color: #f1f5f9; color: #64748b; padding: 12px; text-align: center; font-size: 12px;">
                        Infodive IT Admin &bull; Notificação Automática de Lead
                    </div>
                </div>
            """.formatted(
                escapeHtml(lead.getNomeCompleto()),
                escapeHtml(lead.getEmail()),
                escapeHtml(lead.getEmail()),
                escapeHtml(lead.getTelefone() != null ? lead.getTelefone() : "N/A"),
                escapeHtml(lead.getEmpresa()),
                escapeHtml(lead.getCargo() != null ? lead.getCargo() : "N/A"),
                escapeHtml(produtoNome),
                dataHora,
                escapeHtml(lead.getMensagem() != null ? lead.getMensagem() : "Nenhuma mensagem fornecida")
            );

            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Notificação por e-mail enviada com sucesso para {} referente ao lead ID: {}", recipientEmail, lead.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de notificação de novo lead (ID: {}): {}", lead.getId(), e.getMessage(), e);
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
