#!/bin/bash

# Script de test de connectivité MySQL
echo "🔍 Test de connectivité MySQL..."

# Test de ping du serveur
echo "📡 Test de ping vers 51.195.11.202..."
ping -c 3 51.195.11.202

echo ""
echo "🔌 Test de connectivité sur le port 3308..."
telnet 51.195.11.202 3308

echo ""
echo "✅ Tests terminés"
