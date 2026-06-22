-- Seed de DESENVOLVIMENTO — fabricantes de exemplo.
-- Idempotente: ON CONFLICT no slug (coluna única) evita duplicar a cada startup.
-- Roda após o Hibernate criar o schema (spring.jpa.defer-datasource-initialization=true).
-- Antes de produção, desabilitar via spring.sql.init.mode=never ou mover para um profile de dev.
INSERT INTO fabricantes (id, slug, nome, descricao, descricao_curta, logo_url, site_oficial, destaque, ordem, ativo, created_at, updated_at)
VALUES
  (gen_random_uuid(), 'ibm', 'IBM', 'Integradora global de tecnologia, nuvem híbrida e IA corporativa.', 'Nuvem híbrida e IA corporativa.', 'https://infodive.com.br/logos/ibm.svg', 'https://www.ibm.com', true, 1, true, now(), now()),
  (gen_random_uuid(), 'fortinet', 'Fortinet', 'Líder global em cibersegurança e redes seguras.', 'Segurança de rede e SD-WAN.', 'https://infodive.com.br/logos/fortinet.svg', 'https://www.fortinet.com', true, 2, true, now(), now()),
  (gen_random_uuid(), 'microsoft', 'Microsoft', 'Plataformas de produtividade, nuvem Azure e segurança.', 'Azure, Microsoft 365 e segurança.', 'https://infodive.com.br/logos/microsoft.svg', 'https://www.microsoft.com', true, 3, true, now(), now()),
  (gen_random_uuid(), 'dell', 'Dell Technologies', 'Infraestrutura, servidores e armazenamento corporativo.', 'Servidores, storage e infraestrutura.', 'https://infodive.com.br/logos/dell.svg', 'https://www.dell.com', false, 4, true, now(), now()),
  (gen_random_uuid(), 'vmware', 'VMware', 'Virtualização, nuvem privada e modernização de aplicações.', 'Virtualização e nuvem privada.', 'https://infodive.com.br/logos/vmware.svg', 'https://www.vmware.com', false, 5, true, now(), now())
ON CONFLICT (slug) DO NOTHING;

-- Soluções (consumidas como "categorias" pelo frontend).
INSERT INTO solucoes (id, slug, titulo, icone, subtitulo_curto, descricao_curta, overview, features, imagem_url, fabricantes_titulo, fabricantes_descricao, ordem, ativo, created_at, updated_at)
VALUES
  (gen_random_uuid(), 'seguranca', 'Segurança', 'shield-check', 'Proteção de ponta a ponta', 'Cibersegurança corporativa, do endpoint à nuvem.', 'Visão completa de segurança: prevenção, detecção e resposta a ameaças com monitoramento contínuo.', '[{"titulo":"Firewall de próxima geração","descricao":"Inspeção profunda de pacotes e prevenção de intrusões."},{"titulo":"SOC 24/7","descricao":"Monitoramento e resposta a incidentes ininterruptos."}]'::jsonb, 'https://infodive.com.br/img/seguranca.jpg', 'Fabricantes homologados', 'Trabalhamos com os líderes de mercado em segurança.', 1, true, now(), now()),
  (gen_random_uuid(), 'infraestrutura', 'Infraestrutura', 'server', 'Base sólida para o seu negócio', 'Servidores, storage e data center de alta disponibilidade.', 'Infraestrutura corporativa resiliente, com arquiteturas redundantes e suporte especializado.', '[{"titulo":"Alta disponibilidade","descricao":"Arquiteturas redundantes com failover automático."},{"titulo":"Storage corporativo","descricao":"Armazenamento escalável e seguro."}]'::jsonb, 'https://infodive.com.br/img/infraestrutura.jpg', 'Fabricantes homologados', 'Parceiros de infraestrutura de classe enterprise.', 2, true, now(), now()),
  (gen_random_uuid(), 'nuvem', 'Nuvem', 'cloud', 'Escale sob demanda', 'Migração e gestão de nuvem híbrida e multi-cloud.', 'Estratégia de nuvem ponta a ponta: migração, governança e otimização de custos.', '[{"titulo":"Nuvem híbrida","descricao":"Integração entre on-premises e nuvem pública."},{"titulo":"FinOps","descricao":"Governança e otimização de custos em nuvem."}]'::jsonb, 'https://infodive.com.br/img/nuvem.jpg', 'Fabricantes homologados', 'Especialistas multi-cloud.', 3, true, now(), now())
ON CONFLICT (slug) DO NOTHING;

-- Junção N:N solucoes_fabricantes (resolve ids por slug para ser idempotente).
INSERT INTO solucoes_fabricantes (solucao_id, fabricante_id)
SELECT s.id, f.id
FROM solucoes s
JOIN fabricantes f ON
     (s.slug = 'seguranca'      AND f.slug IN ('ibm', 'fortinet'))
  OR (s.slug = 'infraestrutura' AND f.slug IN ('dell', 'vmware'))
  OR (s.slug = 'nuvem'          AND f.slug IN ('ibm', 'microsoft'))
ON CONFLICT DO NOTHING;

-- Administrador de teste para desenvolvimento (allowlist)
INSERT INTO admins_autorizados (id, email, nome, ativo, criado_em)
VALUES (gen_random_uuid(), 'admin@infodive.com.br', 'Admin Teste', true, now())
ON CONFLICT (email) DO NOTHING;
