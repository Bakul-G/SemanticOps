# SemanticOps

AI-powered event-driven incident classification and orchestration platform using Kafka, Spring Boot, OpenAI embeddings, PostgreSQL + pgvector, and semantic memory retrieval.

---

# Overview

SemanticOps is an event-driven AI decision engine that ingests operational alerts through Kafka, semantically retrieves similar historical incidents using vector embeddings, classifies incoming events using an LLM, and executes deterministic operational workflows.

The project demonstrates:

- AI-assisted incident classification
- Semantic retrieval using vector embeddings
- Event-driven architecture
- Context-aware decision making
- Retrieval-Augmented Generation (RAG)-style workflows
- Hybrid AI + deterministic orchestration

---

# Architecture

```text
                +----------------+
                | Kafka Producer |
                +--------+-------+
                         |
                         v
                +----------------+
                | Kafka Consumer |
                +--------+-------+
                         |
                         v
                +----------------------+
                | Agent Orchestrator   |
                +----------+-----------+
                           |
          +----------------+----------------+
          |                                 |
          v                                 v
+-------------------+           +----------------------+
| Embedding Service |           | OpenAI Classification|
+---------+---------+           +----------+-----------+
          |                                 |
          v                                 v
+------------------------------------------------------+
| PostgreSQL + pgvector Semantic Memory Store          |
+------------------------------------------------------+
                           |
                           v
                +----------------------+
                | Action Executor      |
                +----------------------+
```

---

# Tech Stack

| Component | Technology |
|---|---|
| Backend | Java 21 |
| Framework | Spring Boot |
| Messaging | Apache Kafka |
| AI Models | OpenAI GPT-4o-mini |
| Embeddings | text-embedding-3-small |
| Database | PostgreSQL |
| Vector Search | pgvector |
| Containerization | Docker |
| Build Tool | Maven |

---

# Core Features

## 1. Event-Driven Architecture

Operational events are asynchronously ingested through Kafka topics.

Example event:

```json
{
  "eventId":"7",
  "message":"Transaction failed multiple times",
  "timestamp":"2026-05-10T17:02:00"
}
```

---

## 2. Semantic Retrieval

Incoming alerts are converted into vector embeddings and matched against historical incidents using pgvector similarity search.

Example:

```text
"Payment timeout occurred once"
≈
"Payment service timeout occurred 3 times"
```

---

## 3. Context-Aware Classification

Relevant historical incidents are injected into the LLM prompt before classification.

Example context:

```text
Past events:
[2026-04-18T10:02] Transaction failed multiple times → HIGH
[2026-04-18T10:00] Payment service timeout occurred 3 times → MEDIUM

Now classify:
Transaction failed multiple times
```

---

## 4. AI-Assisted Severity Classification

The system classifies events into:

- LOW
- MEDIUM
- HIGH

Example output:

```json
{
  "severity":"HIGH",
  "reason":"consistent failures"
}
```

---

## 5. Deterministic Action Execution

Actions are executed using Java orchestration logic.

Example:

```text
HIGH   -> Create Incident
MEDIUM -> Send Slack Alert
LOW    -> Log Event
```

---

# Project Structure

```text
src/main/java/com/example
│
├── client
│   ├── OpenAIClient.java
│   └── EmbeddingClient.java
│
├── config
│   └── AgentConfig.java
│
├── kafka
│   └── KafkaListenerService.java
│
├── model
│   ├── AlertEvent.java
│   └── EventDecision.java
│
├── repository
│   ├── EventDecisionRepository.java
│   ├── VectorSearchRepository.java
│   └── VectorSearchRepositoryImpl.java
│
├── services
│   ├── AgentOrchestrator.java
│   ├── ActionExecutor.java
│   └── PromptBuilder.java
│
└── EventDrivenApp.java
```

---

# Setup Instructions

## 1. Clone Repository

```bash
git clone https://github.com/<your-username>/semanticops.git
cd semanticops
```

---

# 2. Start Infrastructure

## Docker Compose

```yaml
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  postgres:
    image: pgvector/pgvector:pg15
    container_name: agent-postgres
    environment:
      POSTGRES_DB: agentdb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
```

Start containers:

```bash
docker-compose up -d
```

---

# 3. Enable pgvector

```bash
docker exec -it agent-postgres psql -U user -d agentdb
```

Run:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

---

# 4. Create Database Table

```sql
CREATE TABLE event_decision (
    id SERIAL PRIMARY KEY,
    event_id VARCHAR(255),
    message TEXT,
    severity VARCHAR(20),
    reason TEXT,
    timestamp TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    embedding vector(1536)
);
```

---

# 5. Configure Application

## application.yml

```yaml
openai:
  api-key: YOUR_OPENAI_API_KEY

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/agentdb
    username: user
    password: password

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: agent-group
      auto-offset-reset: earliest
```

---

# 6. Run Application

```bash
mvn spring-boot:run
```

---

# 7. Produce Kafka Events

```bash
kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic alerts-topic
```

Example events:

```json
{"eventId":"1","message":"Payment service timeout occurred 3 times","timestamp":"2026-04-18T10:00:00"}
```

```json
{"eventId":"2","message":"Transaction failed multiple times","timestamp":"2026-04-18T10:02:00"}
```

---

# Example Execution Flow

```text
Received Event
    ↓
Generate Embedding
    ↓
Retrieve Similar Events
    ↓
Build Context
    ↓
Call LLM
    ↓
Classify Severity
    ↓
Execute Action
    ↓
Persist Incident
```

---

# Example Logs

```text
Past events found: 5

Past events:
[2026-04-18T10:02] Transaction failed multiple times → HIGH
[2026-04-18T10:00] Payment service timeout occurred 3 times → MEDIUM

Now classify:
Transaction failed multiple times

Severity: HIGH
Reason: consistent failures
```

---

# Future Enhancements

## Planned Improvements

- Similarity threshold filtering
- Slack integration
- Jira/PagerDuty integration
- Incident dashboards
- Multi-agent orchestration
- Dynamic tool selection
- Semantic deduplication
- Hybrid BM25 + vector retrieval
- Temporal weighting of incidents
- Autonomous remediation workflows

---

# Key Engineering Concepts Demonstrated

- Event-driven systems
- Semantic search
- Retrieval-Augmented Generation (RAG)
- AI orchestration
- Vector databases
- Embedding-based retrieval
- Prompt engineering
- Context-aware inference
- Deterministic workflow orchestration
- Hybrid AI systems

---

# Why This Project Matters

Most AI projects stop at simple API integrations.

SemanticOps focuses on building production-style AI system architecture:

- semantic memory
- orchestration
- retrieval pipelines
- deterministic execution
- operational automation

The project combines backend engineering, distributed systems, and applied AI into a unified workflow.

---

# License

MIT License
