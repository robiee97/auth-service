# CI/CD Pipeline with GitHub Actions, Helm, ACR & AKS for Spring Boot App

This project demonstrates how to build a CI/CD pipeline for a Spring Boot application using GitHub Actions. The pipeline builds a Docker image on every merge to the `main` branch, pushes it to Azure Container Registry (ACR), and deploys it to Azure Kubernetes Service (AKS) using Helm charts.

---

## 🚀 Technologies Used

- Spring Boot (Java 17)
- GitHub Actions
- Docker
- Helm
- Azure Container Registry (ACR)
- Azure Kubernetes Service (AKS)
- Maven

---

## 📁 Project Structure

.
├── Dockerfile
├── helm/
│ └── <your-app-name>/
├── .github/
│ └── workflows/
│ └── cicd.yml
└── src/




---

## 🧱 Step-by-Step Guide

### 1. Dockerize Your Spring Boot App

Create a `Dockerfile` in the root:

```Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/crud-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```
2. Create a Helm Chart
bash
Copy
Edit
brew install helm
helm create <your-app-name>
Customize values as needed in the Helm chart.

3. Set Up GitHub Actions Workflow
Create the following file: .github/workflows/cicd.yml

cicd.yaml
```
name: Build and Deploy to AKS

on:
  push:
    branches:
      - main

env:
  AZURE_CONTAINER_REGISTRY: <YOUR_ACR_REPO>
  IMAGE_NAME: <IMAGE_NAME>

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout source
        uses: actions/checkout@v3

      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build JAR using Maven
        run: mvn clean package -DskipTests

      - name: Log in to Azure
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}

      - name: Log in to ACR
        run: az acr login --name <Your registry name>

      - name: Build and Push Docker image
        run: |
          docker build -t $AZURE_CONTAINER_REGISTRY/$IMAGE_NAME:${{ github.sha }} .
          docker push $AZURE_CONTAINER_REGISTRY/$IMAGE_NAME:${{ github.sha }}

      - name: Set up Helm
        uses: azure/setup-helm@v3

      - name: Set Kubernetes context
        uses: azure/aks-set-context@v3
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
          cluster-name: <Your K8S cluster name>
          resource-group: <Your resource group name>

      - name: Deploy using Helm
        run: |
          helm upgrade --install <your-deployment-release-name> ./<your-helm-folder-name> \
            --set image.repository=$AZURE_CONTAINER_REGISTRY/$IMAGE_NAME \
            --set image.tag=${{ github.sha }} \
            --set service.type=LoadBalancer
```
🔐 Create Repository Secrets
Generate Azure credentials:

az ad sp create-for-rbac --name "github-actions-sp" \
  --role contributor \
  --scopes /subscriptions/<SUBSCRIPTIONID>/resourceGroups/<RESOURCE_GROUP> \
  --sdk-auth
Paste the JSON output in your GitHub repository secrets as AZURE_CREDENTIALS.

☁️ Azure CLI Setup
1. Create Resource Group
    az group create --name robinResourceGroup --location eastus

4. Create ACR (Azure Container Registry)
   az acr create --resource-group robinResourceGroup \
  --name robiee97 \
  --sku Basic \
  --admin-enabled true

5. Create AKS Cluster
az aks create \
  --resource-group robinResourceGroup \
  --name robiee97-crud \
  --node-count 2 \
  --enable-addons monitoring \
  --generate-ssh-keys
6. Get AKS Credentials Locally

az aks get-credentials --resource-group robinResourceGroup --name robiee97-crud
🔗 Link ACR to AKS

Give AKS permission to pull from ACR:
az aks update -n robiee97-crud -g robinResourceGroup --attach-acr robiee97


📌 Useful Commands
Check AKS nodes:

kubectl get nodes

Check services:
kubectl get svc

Stop AKS cluster:
az aks stop --resource-group robinResourceGroup --name robiee97-crud
Start AKS cluster:
az aks start --resource-group robinResourceGroup --name robiee97-crud


