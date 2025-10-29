package org.example.mcpclient.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AiAgent {
    private ChatClient chatClient;

    public AiAgent(ChatClient.Builder chatClient, ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClient
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultSystem("""
                    Tu es un assistant e-commerce multivendeur intelligent qui aide les utilisateurs à trouver des produits et gérer leurs commandes.
                    
                    RÈGLES IMPORTANTES:
                    1. Pour les SALUTATIONS et QUESTIONS GÉNÉRALES, réponds directement sans utiliser d'outils :
                       - "Bonjour", "Salut", "Hello" → Réponds poliment et présente-toi
                       - "Comment ça va ?", "Comment allez-vous ?" → Réponds de manière amicale
                       - Questions sur tes capacités → Explique ce que tu peux faire
                    
                    2. Pour les QUESTIONS SPÉCIFIQUES AUX PRODUITS, utilise les outils appropriés :
                       - "produits biologiques" ou "organic" → getOrganicProducts
                       - "rechercher [produit]" ou "chercher [produit]" → searchProducts
                       - "riche en protéines" → getHighProteinProducts
                       - "riche en fibres" → getHighFiberProducts
                       - "faible en sucre" → getLowSugarProducts
                       - "panier" ou "cart" → getCart
                       - "profil utilisateur" → getUserProfile
                    
                    3. Quand tu utilises un outil, base ta réponse uniquement sur les données retournées
                    4. Réponds en français de manière claire, naturelle et conversationnelle
                    5. Sois toujours poli et professionnel
                    
                    Exemples:
                    User: "Bonjour"
                    Assistant: "Bonjour ! Je suis votre assistant IA AgriMarket. Je peux vous aider à trouver des produits, créer des plans de repas, gérer votre panier et bien plus encore. Comment puis-je vous aider aujourd'hui ?"
                    
                    User: "Combien de produits biologiques avez-vous ?"
                    Assistant: [Appelle getOrganicProducts] → "Nous avons actuellement X produits biologiques disponibles dans notre catalogue."
                    """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(
                        MessageWindowChatMemory.builder().maxMessages(30).build()
                ).build())
                .build();
    }

    public String askLLM(String query){
        try {
            String response = chatClient
                    .prompt()
                    .user(query)
                    .call()
                    .content();
            
            // Nettoyer la réponse pour éviter d'afficher du JSON brut
            if (response != null && response.trim().startsWith("{")) {
                // Si la réponse commence par du JSON, essayer de la reformater
                return "Je traite votre demande... Veuillez patienter.";
            }
            
            return response;
        } catch (Exception e) {
            return "Désolé, une erreur s'est produite lors du traitement de votre demande. Veuillez réessayer.";
        }
    }
}