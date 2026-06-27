#!/bin/bash

# ==============================================================
# setup.sh - Provisionamento automático da VM
# Instala: Docker, Docker Compose, Git
# Clona o projeto e sobe os ambientes de homolog e prod
# ==============================================================

set -e

REPO_URL="https://github.com/felipe-noll/projeto.git"
PROJECT_DIR="$HOME/projeto"

echo "================================================="
echo " Iniciando provisionamento da VM"
echo "================================================="

echo ""
echo "[1/6] Atualizando pacotes do sistema..."
sudo apt update -y && sudo apt upgrade -y

echo ""
echo "[2/6] Instalando dependências base..."
sudo apt install -y curl gnupg ca-certificates lsb-release git unzip
echo "Git instalado: $(git --version)"

echo ""
echo "[3/6] Instalando Docker..."
sudo apt remove -y docker docker-engine docker.io containerd runc 2>/dev/null || true
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
    sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | \
    sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update -y
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker "$USER" 2>/dev/null || echo "Aviso: nao foi possivel adicionar $USER ao grupo docker. Use sudo docker para executar os comandos."
echo "Docker instalado: $(sudo docker --version)"
echo "Docker Compose instalado: $(sudo docker compose version)"

echo ""
echo "[4/6] Clonando repositório..."
if [ -d "$PROJECT_DIR" ]; then
    echo "Diretório $PROJECT_DIR já existe. Atualizando..."
    cd "$PROJECT_DIR"
    git pull origin main
else
    git clone "$REPO_URL" "$PROJECT_DIR"
    cd "$PROJECT_DIR"
fi
echo "Repositório pronto em: $PROJECT_DIR"

echo ""
echo "[5/6] Subindo ambiente de Homologação..."
cd "$PROJECT_DIR"
sudo docker compose -f docker-compose.homolog.yml up -d --build
echo "Homologação disponível em: http://177.44.248.100:8080"

echo ""
echo "[6/6] Subindo ambiente de Produção..."
sudo docker compose -f docker-compose.prod.yml up -d --build
echo "Produção disponível em: http://177.44.248.100:9080"

echo ""
echo "================================================="
echo " Provisionamento concluído com sucesso!"
echo "================================================="
echo ""
echo "IMPORTANTE: Para usar o Docker sem sudo, execute:"
echo "  newgrp docker"
echo ""
echo "Para verificar os containers rodando:"
echo "  sudo docker ps"
echo ""
echo "Para ver os logs:"
echo "  sudo docker compose -f docker-compose.homolog.yml logs -f"
