# Votacao
## Propósito Geral
O projeto votacao tem como objetivo computar os votos de uma sessão.

## Funcoes de Votacao
- /votacao/cadastraPauta 
  - Cadastra uma sessao
- /votacao/cadastraVoto 
  - computa um voto em uma sessao
  - Para computar um voto, a sessao deve existir e esta aberta.
  - O usuario pode votar apenas uma vez em cada sessao.
  - Valores aceitos:
    - Sim
    - Nao
- /votacao/abrirSessao 
  - Abre uma sessao para votacao
  - O sistema permite abrir varias sessoes simultaneas
  - Cada sessao ficara aberta por 1 minuto. 
  - Apos o fechamento, uma mensagem e enviada para a fila rabbit informando o resultado
- /votacao/sessaoAberta 
  - Verifica o Status de uma secao
- /votacao/fecharSessao 
  - Fecha uma sessao aberta mesmo que aberta a menos de 1 minuto
- /votacao/computarVotos
  - Computa os votos de uma sessao e retorna o resultado. 
  - Nessa chamada, uma sessao fechada pode ser contabilizada
  - Uma mensagem e enviada tambem para fila informando o resultado
- /users
  - Verifica se um cpf e valiado

## Informacoes adicionais
- Dentro do diretorio (Documentacao) foi adicionado um pdf com informacoes sobre cada servico
- Existe um JOB que monitora o tempo que a sessao esta aberta. 
  - Apos 1 minuto, a sessao e fechada.
  - Uma mensagem e enviada para fila informando o resultado

## Possiveis retorno da aplicacao
- ERRO("Erro")
- SUCESSO("Sucesso")
- ID_PAUTA_INEXISTENTE("Id Pauta Inexistente")
- USUARIO_JA_VOTOU("Voto ja computado")
- PAUTA_FECHADA("Pauta Fechada")
- PAUTA_ABERTA("Pauta Aberta")
- ABLE_TO_VOTE("CPF valido")
- UNABLE_TO_VOTE("CPF invalido")

# Teste dos Endpoints 
- Dentro da pasta postman existe um arquivo json que pode ser importado para o POSTMAN

## Testes
- Foram criados testes unitarios para cada funcionalidade do sistema
- Tambem foi criado um teste de performance simples que recebe varias requisicoes de voto enquando a sesao esta aberta.
- Ao final do teste de performance, uma mensagem e enviada para a fila informando o resultado

## Armazenamento
- Como nao foi definido qual banco de dados deveria ser utilizado na aplicacao, realizei o armazenamento em arquivos.
