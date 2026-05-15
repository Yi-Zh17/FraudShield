# FraudShield

A credit card fraud detection web application built with Spring Boot and ONNX Runtime. Users submit transaction details through a web form, and a trained neural network predicts the likelihood of fraud in real time. Results are persisted to Azure SQL Server for audit and analysis.

## Architecture

```
Browser (Thymeleaf + Tailwind CSS)
        │
        ▼
FraudController  ──────►  FraudCheckService  ──────►  FraudAuditRepository
                                │                           │
                                ▼                           ▼
                        FraudCheckUtil              Azure SQL Server
                        (feature engineering)        (fraud_audit_logs)
                                │
                                ▼
                      ModelPredictionService
                        (ONNX Runtime)
                                │
                        model.onnx (NN classifier)
```

### Feature pipeline

Raw form inputs are transformed into a 75-dimensional feature vector before inference:

| Feature | Encoding |
|---|---|
| Amount, City Population, Age | Z-score normalization |
| Category (14 values) | One-hot encoding |
| State (51 values) | One-hot encoding |
| Merchant | Frequency encoding |
| Gender | Binary encoding |
| Job | Frequency encoding |
| Transaction Hour | Cyclic encoding (sin/cos) |
| Day of Week | Cyclic encoding (sin/cos) |

The ONNX model outputs a raw logit, which is passed through a sigmoid to produce a probability. Transactions at or above 0.5 are flagged as fraudulent.

## Tech Stack

- **Java 17** / **Spring Boot 3.5.12** — web framework, JPA, MVC
- **Thymeleaf** — server-side HTML templating
- **Tailwind CSS v4** — utility-first styling
- **ONNX Runtime 1.22** — ML model inference (Java bindings)
- **Azure SQL Server** — persistent audit log storage
- **Azure Identity** — passwordless Entra ID authentication
- **Docker** — multi-stage builds with Spring Boot layer extraction
- **Kubernetes** — deployment manifests included
- **GitHub Actions** — CI/CD pipeline to Azure Web App for Containers

## Running Locally

### Prerequisites

- Java 17+
- Maven (or use the included `mvnw` wrapper)
- Access to an Azure SQL Server instance (or modify `application.properties` to point elsewhere)

### Environment variables

| Variable | Default | Description |
|---|---|---|
| `DB_SERVER_NAME` | `fraudshield` | Azure SQL Server hostname |
| `DB_NAME` | `fraudshield` | Database name |

The app connects via Azure `ActiveDirectoryDefault` authentication. If running locally, run `az login` first.

### Build and run

```bash
./mvnw spring-boot:run
```

The app starts at `http://localhost:8080`.

### Docker

```bash
docker build -t fraudshield .
docker run -p 8080:8080 \
  -e DB_SERVER_NAME=your-server \
  -e DB_NAME=fraudshield \
  fraudshield
```

Or use Docker Compose:

```bash
docker compose up
```

## Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | Landing page |
| `GET` | `/fraud` | Fraud check form with dynamic dropdowns |
| `POST` | `/fraud/check` | Submit transaction for fraud prediction |

## Project Structure

```
src/main/java/FraudShield/FraudShield/
├── FraudShieldApplication.java       # Entry point
├── Controller/
│   ├── IndexController.java          # GET /
│   └── FraudController.java          # GET/POST /fraud
├── Entity/
│   └── FraudAudit.java               # JPA entity → fraud_audit_logs
├── Model/
│   ├── FraudCheckRequest.java        # Input record
│   └── FraudCheckResponse.java       # Output record
├── Repository/
│   └── FraudAuditRepository.java     # Spring Data JPA repository
└── Service/
    ├── FraudCheckService.java        # Orchestration & persistence
    ├── FraudCheckUtil.java           # Feature engineering (75-dim vector)
    ├── ModelPredictionService.java   # ONNX Runtime wrapper
    ├── JobFreqService.java           # Job frequency lookup
    └── MerchantFreqService.java      # Merchant frequency lookup

src/main/resources/
├── application.properties            # DB config, JPA settings
├── static/
│   ├── model.onnx                    # Trained neural network (20 KB)
│   ├── model.onnx.data               # ONNX external data (58 KB)
│   ├── job_frequencies.json          # Job frequency mappings
│   └── merchant_frequencies.json     # Merchant frequency mappings
└── templates/
    ├── index.html                    # Home page
    ├── fraud.html                    # Input form
    └── fraud-result.html             # Prediction result
```

## Deployment

### GitHub Actions → Azure

On push to `main`, the pipeline:
1. Builds a Docker image with `docker/build-push-action`
2. Pushes it to GitHub Container Registry (`ghcr.io`)
3. Deploys to Azure Web App for Containers via `azure/webapps-deploy`

## License

This project is for educational/demonstration purposes.
