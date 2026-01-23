package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.message.ConversationDto;
import com.elzocodeur.campusmaster.application.dto.message.MessageDto;
import com.elzocodeur.campusmaster.application.dto.message.SendMessageRequest;
import com.elzocodeur.campusmaster.domain.entity.Conversation;
import com.elzocodeur.campusmaster.domain.entity.Message;
import com.elzocodeur.campusmaster.domain.entity.MessageTag;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.MessageType;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.ConversationRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.MessageRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.MessageTagRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageriePriveeService {

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageTagRepository messageTagRepository;

    @Transactional
    public MessageDto envoyerMessagePrive(SendMessageRequest request) {
        User expediteur = userRepository.findById(request.getExpediteurId())
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable"));

        User destinataire = userRepository.findById(request.getDestinataireId())
                .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));

        // Trouver ou créer la conversation
        Conversation conversation = conversationRepository
                .findByParticipants(expediteur.getId(), destinataire.getId())
                .orElseGet(() -> {
                    Conversation newConv = Conversation.builder()
                            .participant1(expediteur)
                            .participant2(destinataire)
                            .archivee(false)
                            .build();
                    return conversationRepository.save(newConv);
                });

        // Récupérer les tags
        Set<MessageTag> tags = new HashSet<>();
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            tags = messageTagRepository.findByNomIn(request.getTags()).stream()
                    .collect(Collectors.toSet());
        }

        // Créer le message
        Message message = Message.builder()
                .contenu(request.getContenu())
                .expediteur(expediteur)
                .destinataire(destinataire)
                .conversation(conversation)
                .typeMessage(MessageType.PRIVE)
                .dateEnvoi(LocalDateTime.now())
                .estLu(false)
                .tags(tags)
                .build();

        message = messageRepository.save(message);

        // Mettre à jour la conversation
        conversation.setDernierMessageDate(message.getDateEnvoi());
        conversation.setDernierMessageContenu(message.getContenu());
        conversationRepository.save(conversation);

        return toMessageDto(message);
    }

    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsByUser(Long userId) {
        return conversationRepository.findActiveConversationsByUserId(userId).stream()
                .map(this::toConversationDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ConversationDto getConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.getParticipant1().getId().equals(userId) &&
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }

        return toConversationDto(conversation);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.getParticipant1().getId().equals(userId) &&
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }

        return messageRepository.findByConversationIdOrderByDateEnvoiDesc(conversationId).stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void marquerCommeLu(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        if (!message.getDestinataire().getId().equals(userId)) {
            throw new RuntimeException("Seul le destinataire peut marquer le message comme lu");
        }

        message.setEstLu(true);
        messageRepository.save(message);
    }

    @Transactional
    public void marquerTousCommeLus(Long conversationId, Long userId) {
        List<Message> messages = messageRepository.findByConversationIdOrderByDateEnvoiDesc(conversationId);

        messages.stream()
                .filter(m -> m.getDestinataire().getId().equals(userId) && !m.getEstLu())
                .forEach(m -> {
                    m.setEstLu(true);
                    messageRepository.save(m);
                });
    }

    @Transactional
    public void archiverConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.getParticipant1().getId().equals(userId) &&
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }

        conversation.setArchivee(true);
        conversationRepository.save(conversation);
    }

    @Transactional
    public void desarchiverConversation(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation introuvable"));

        // Vérifier que l'utilisateur fait partie de la conversation
        if (!conversation.getParticipant1().getId().equals(userId) &&
            !conversation.getParticipant2().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette conversation");
        }

        conversation.setArchivee(false);
        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public Integer getNombreMessagesNonLus(Long userId) {
        return messageRepository.countMessagesNonLus(userId);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesNonLus(Long userId) {
        return messageRepository.findByDestinataireIdAndEstLuFalse(userId).stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void supprimerMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        // Seul l'expéditeur peut supprimer son message
        if (!message.getExpediteur().getId().equals(userId)) {
            throw new RuntimeException("Seul l'expéditeur peut supprimer ce message");
        }

        messageRepository.delete(message);
    }

    private MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .contenu(message.getContenu())
                .estLu(message.getEstLu())
                .dateEnvoi(message.getDateEnvoi())
                .typeMessage(message.getTypeMessage().name())
                .expediteurId(message.getExpediteur().getId())
                .expediteurNom(message.getExpediteur().getLastName() + " " + message.getExpediteur().getFirstName())
                .destinataireId(message.getDestinataire() != null ? message.getDestinataire().getId() : null)
                .destinataireNom(message.getDestinataire() != null ?
                        message.getDestinataire().getLastName() + " " + message.getDestinataire().getFirstName() : null)
                .conversationId(message.getConversation() != null ? message.getConversation().getId() : null)
                .groupeId(message.getGroupe() != null ? message.getGroupe().getId() : null)
                .groupeNom(message.getGroupe() != null ? message.getGroupe().getNom() : null)
                .tags(message.getTags() != null ?
                        message.getTags().stream().map(MessageTag::getNom).collect(Collectors.toSet()) : null)
                .build();
    }

    private ConversationDto toConversationDto(Conversation conversation) {
        Long userId = conversation.getParticipant1().getId();
        Integer nombreMessagesNonLus = messageRepository.countMessagesNonLusByConversation(
                conversation.getId(), userId);

        return ConversationDto.builder()
                .id(conversation.getId())
                .participant1Id(conversation.getParticipant1().getId())
                .participant1Nom(conversation.getParticipant1().getLastName() + " " +
                        conversation.getParticipant1().getFirstName())
                .participant2Id(conversation.getParticipant2().getId())
                .participant2Nom(conversation.getParticipant2().getLastName() + " " +
                        conversation.getParticipant2().getFirstName())
                .dernierMessageDate(conversation.getDernierMessageDate())
                .dernierMessageContenu(conversation.getDernierMessageContenu())
                .nombreMessagesNonLus(nombreMessagesNonLus)
                .archivee(conversation.getArchivee())
                .build();
    }
}
