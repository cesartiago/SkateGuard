# SkateGuard - Projeto para disciplina de Sistemas Distribuídos

O SkateGuard é um projeto dedicado a aprimorar a segurança dos skatistas, especialmente aqueles envolvidos em modalidades de corrida. O sistema inclui uma aplicação móvel desenvolvida em Kotlin e um sistema distribuído que permite aos skatistas alertarem uns aos outros em tempo real quando caem, compartilhando suas coordenadas exatas de queda.

## Componentes Principais

### Aplicação Móvel (Android)

- **Desenvolvimento em Kotlin:** A aplicação móvel é desenvolvida em Kotlin, proporcionando uma experiência moderna e eficiente para os skatistas.

- **Monitoramento da Aceleração com Biblioteca Nativa do Android:** Utiliza a biblioteca nativa do Android para obter aceleração e monitorar a atividade do dispositivo. A detecção de quedas é realizada através da integração direta com as funcionalidades do sistema operacional, proporcionando uma análise mais eficiente e precisa.

- **Integração com Geolocalização:** Integra a API de geolocalização, como a Google Maps API, para obter coordenadas precisas do local da queda.

### Protocolo de Comunicação

- **MQTT (Message Queuing Telemetry Transport):** Estabelece um protocolo de comunicação entre dispositivos para alertas de queda, confirmações e atualizações de localização.

### Fornecedor de Broker MQTT

- **HiveMQ:** Utiliza um fornecedor de broker MQTT, como HiveMQ, para facilitar a comunicação assíncrona entre os dispositivos dos skatistas, garantindo a entrega confiável de mensagens e a sincronização eficiente entre os participantes.

### Temporização

- **Mecanismo de Temporização:** Implementa um mecanismo de temporização no lado do cliente para determinar se o skatista está imóvel por um período suficientemente longo para indicar uma queda, evitando alertas falsos por movimentos temporários ou quedas leves.

### API de Geolocalização

- **Integração com Google Maps API:** Utiliza a Google Maps API para obter informações precisas sobre a localização do skatista. A localização é compartilhada apenas durante uma queda, notificando outros skatistas próximos.

### Interface do Usuário

- **Interface Intuitiva:** Desenvolve uma interface intuitiva e amigável para a aplicação móvel.

- **Notificações Visuais:** Exibe notificações visuais quando um skatista cai, utilizando mapas para representar graficamente a localização do skatista caído.

### Sistema de Notificação

- **Firebase Cloud Messaging (FCM):** Implementa o FCM para enviar notificações push, oferecendo opções de aceitar ou rejeitar o alerta e promovendo a interação entre a comunidade de skatistas.

## Configuração do Projeto

### Configuração do Ambiente

- Certifique-se de ter o Android Studio instalado.
- Instale o plugin Kotlin no Android Studio.

### Configuração do Projeto

- Abra o projeto no Android Studio.
- Verifique se as dependências do Kotlin estão configuradas corretamente no arquivo `build.gradle`.

### Execução do Projeto

- Configure a classe principal para a execução, geralmente aquela que contém a função `main`.
- Execute o projeto e verifique a saída no console.

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para relatar problemas, sugerir melhorias ou contribuir diretamente para o projeto.
