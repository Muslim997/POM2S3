package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.message.CreateGroupeRequest;
import com.elzocodeur.campusmaster.application.dto.message.GroupeMessageDto;
import com.elzocodeur.campusmaster.application.dto.message.MessageDto;
import com.elzocodeur.campusmaster.application.dto.message.SendMessageRequest;
import com.elzocodeur.campusmaster.domain.entity.*;
import com.elzocodeur.campusmaster.domain.enums.MessageType;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.CoursRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.GroupeMessageRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.MatiereRepository;
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
public class MessagerieGroupeService {

    private final GroupeMessageRepository groupeMessageRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MatiereRepository matiereRepository;
    private final CoursRepository coursRepository;
    private final MessageTagRepository messageTagRepository;

    @Transactional
    public GroupeMessageDto creerGroupe(CreateGroupeRequest request) {
        User createur = userRepository.findById(request.getCreateurId())
                .orElseThrow(() -> new RuntimeException("Créateur introuvable"));

        Matiere matiere = null;
        if (request.getMatiereId() != null) {
            matiere = matiereRepository.findById(request.getMatiereId())
                    .orElseThrow(() -> new RuntimeException("Matière introuvable"));
        }

        Cours cours = null;
        if (request.getCoursId() != null) {
            cours = coursRepository.findById(request.getCoursId())
                    .orElseThrow(() -> new RuntimeException("Cours introuvable"));
        }

        // Récupérer les membres
        Set<User> membres = new HashSet<>();
        membres.add(createur); // Le créateur est toujours membre

        if (request.getMembresIds() != null && !request.getMembresIds().isEmpty()) {
            List<User> additionalMembres = userRepository.findAllById(request.getMembresIds());
            membres.addAll(additionalMembres);
        }

        GroupeMessage groupe = GroupeMessage.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .matiere(matiere)
                .cours(cours)
                .createur(createur)
                .membres(membres)
                .actif(true)
                .build();

        groupe = groupeMessageRepository.save(groupe);

        return toGroupeMessageDto(groupe);
    }

    @Transactional
    public MessageDto envoyerMessageGroupe(SendMessageRequest request) {
        User expediteur = userRepository.findById(request.getExpediteurId())
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable"));

        GroupeMessage groupe = groupeMessageRepository.findById(request.getGroupeId())
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'expéditeur est membre du groupe
        if (!groupeMessageRepository.isUserMembre(groupe.getId(), expediteur.getId())) {
            throw new RuntimeException("Vous devez être membre du groupe pour envoyer un message");
        }

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
                .groupe(groupe)
                .typeMessage(MessageType.GROUPE)
                .dateEnvoi(LocalDateTime.now())
                .estLu(false)
                .tags(tags)
                .build();

        message = messageRepository.save(message);

        return toMessageDto(message);
    }

    @Transactional(readOnly = true)
    public List<GroupeMessageDto> getGroupesByUser(Long userId) {
        return groupeMessageRepository.findActiveGroupesByMembreId(userId).stream()
                .map(this::toGroupeMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupeMessageDto> getGroupesByMatiere(Long matiereId) {
        return groupeMessageRepository.findActiveByMatiereId(matiereId).stream()
                .map(this::toGroupeMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupeMessageDto> getGroupesByCours(Long coursId) {
        return groupeMessageRepository.findActiveByCoursId(coursId).stream()
                .map(this::toGroupeMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupeMessageDto getGroupe(Long groupeId, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est membre du groupe
        if (!groupeMessageRepository.isUserMembre(groupeId, userId)) {
            throw new RuntimeException("Accès non autorisé à ce groupe");
        }

        return toGroupeMessageDto(groupe);
    }

    @Transactional(readOnly = true)
    public List<MessageDto> getMessagesGroupe(Long groupeId, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est membre du groupe
        if (!groupeMessageRepository.isUserMembre(groupeId, userId)) {
            throw new RuntimeException("Accès non autorisé à ce groupe");
        }

        return messageRepository.findByGroupeIdOrderByDateEnvoiDesc(groupeId).stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void ajouterMembres(Long groupeId, Set<Long> membresIds, Long requesterId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que le requester est le créateur ou un membre
        if (!groupe.getCreateur().getId().equals(requesterId) &&
            !groupeMessageRepository.isUserMembre(groupeId, requesterId)) {
            throw new RuntimeException("Accès non autorisé");
        }

        List<User> nouveauxMembres = userRepository.findAllById(membresIds);
        groupe.getMembres().addAll(nouveauxMembres);
        groupeMessageRepository.save(groupe);
    }

    @Transactional
    public void retirerMembre(Long groupeId, Long membreId, Long requesterId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que le requester est le créateur ou se retire lui-même
        if (!groupe.getCreateur().getId().equals(requesterId) && !membreId.equals(requesterId)) {
            throw new RuntimeException("Seul le créateur peut retirer d'autres membres");
        }

        // Ne pas permettre de retirer le créateur
        if (groupe.getCreateur().getId().equals(membreId)) {
            throw new RuntimeException("Le créateur ne peut pas être retiré du groupe");
        }

        User membre = userRepository.findById(membreId)
                .orElseThrow(() -> new RuntimeException("Membre introuvable"));

        groupe.getMembres().remove(membre);
        groupeMessageRepository.save(groupe);
    }

    @Transactional
    public void desactiverGroupe(Long groupeId, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est le créateur
        if (!groupe.getCreateur().getId().equals(userId)) {
            throw new RuntimeException("Seul le créateur peut désactiver le groupe");
        }

        groupe.setActif(false);
        groupeMessageRepository.save(groupe);
    }

    @Transactional
    public void activerGroupe(Long groupeId, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est le créateur
        if (!groupe.getCreateur().getId().equals(userId)) {
            throw new RuntimeException("Seul le créateur peut activer le groupe");
        }

        groupe.setActif(true);
        groupeMessageRepository.save(groupe);
    }

    @Transactional
    public void modifierGroupe(Long groupeId, String nom, String description, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est le créateur
        if (!groupe.getCreateur().getId().equals(userId)) {
            throw new RuntimeException("Seul le créateur peut modifier le groupe");
        }

        if (nom != null && !nom.isBlank()) {
            groupe.setNom(nom);
        }
        if (description != null) {
            groupe.setDescription(description);
        }

        groupeMessageRepository.save(groupe);
    }

    @Transactional
    public void supprimerGroupe(Long groupeId, Long userId) {
        GroupeMessage groupe = groupeMessageRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

        // Vérifier que l'utilisateur est le créateur
        if (!groupe.getCreateur().getId().equals(userId)) {
            throw new RuntimeException("Seul le créateur peut supprimer le groupe");
        }

        // Supprimer tous les messages du groupe
        messageRepository.findByGroupeIdOrderByDateEnvoiDesc(groupeId)
                .forEach(messageRepository::delete);

        groupeMessageRepository.delete(groupe);
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

    private GroupeMessageDto toGroupeMessageDto(GroupeMessage groupe) {
        Integer nombreMembres = groupeMessageRepository.countMembresByGroupeId(groupe.getId());
        Integer nombreMessages = groupeMessageRepository.countMessagesByGroupeId(groupe.getId());

        return GroupeMessageDto.builder()
                .id(groupe.getId())
                .nom(groupe.getNom())
                .description(groupe.getDescription())
                .matiereId(groupe.getMatiere() != null ? groupe.getMatiere().getId() : null)
                .matiereNom(groupe.getMatiere() != null ? groupe.getMatiere().getLibelle() : null)
                .coursId(groupe.getCours() != null ? groupe.getCours().getId() : null)
                .coursNom(groupe.getCours() != null ? groupe.getCours().getTitre() : null)
                .createurId(groupe.getCreateur().getId())
                .createurNom(groupe.getCreateur().getLastName() + " " + groupe.getCreateur().getFirstName())
                .nombreMembres(nombreMembres)
                .nombreMessages(nombreMessages)
                .actif(groupe.getActif())
                .createdAt(groupe.getCreatedAt())
                .build();
    }
}
