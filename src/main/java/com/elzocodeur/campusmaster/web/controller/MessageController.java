package com.elzocodeur.campusmaster.web.controller;

import com.elzocodeur.campusmaster.application.dto.message.*;
import com.elzocodeur.campusmaster.application.service.MessagerieGroupeService;
import com.elzocodeur.campusmaster.application.service.MessageriePriveeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messagerie", description = "Endpoints pour la messagerie interne (privée et groupes)")
public class MessageController {

    private final MessageriePriveeService messageriePriveeService;
    private final MessagerieGroupeService messagerieGroupeService;

    // ========== MESSAGERIE PRIVÉE ==========

    @PostMapping("/prive")
    @Operation(summary = "Envoyer un message privé")
    public ResponseEntity<MessageDto> envoyerMessagePrive(@Valid @RequestBody SendMessageRequest request) {
        MessageDto message = messageriePriveeService.envoyerMessagePrive(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/conversations/user/{userId}")
    @Operation(summary = "Récupérer toutes les conversations d'un utilisateur")
    public ResponseEntity<List<ConversationDto>> getConversationsByUser(@PathVariable Long userId) {
        List<ConversationDto> conversations = messageriePriveeService.getConversationsByUser(userId);
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/conversations/{conversationId}/user/{userId}")
    @Operation(summary = "Récupérer une conversation spécifique")
    public ResponseEntity<ConversationDto> getConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        ConversationDto conversation = messageriePriveeService.getConversation(conversationId, userId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping("/conversations/{conversationId}/messages/user/{userId}")
    @Operation(summary = "Récupérer tous les messages d'une conversation")
    public ResponseEntity<List<MessageDto>> getMessagesConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        List<MessageDto> messages = messageriePriveeService.getMessagesConversation(conversationId, userId);
        return ResponseEntity.ok(messages);
    }

    @PutMapping("/{messageId}/lire/user/{userId}")
    @Operation(summary = "Marquer un message comme lu")
    public ResponseEntity<Void> marquerCommeLu(
            @PathVariable Long messageId,
            @PathVariable Long userId) {
        messageriePriveeService.marquerCommeLu(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/conversations/{conversationId}/lire-tous/user/{userId}")
    @Operation(summary = "Marquer tous les messages d'une conversation comme lus")
    public ResponseEntity<Void> marquerTousCommeLus(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        messageriePriveeService.marquerTousCommeLus(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/conversations/{conversationId}/archiver/user/{userId}")
    @Operation(summary = "Archiver une conversation")
    public ResponseEntity<Void> archiverConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        messageriePriveeService.archiverConversation(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/conversations/{conversationId}/desarchiver/user/{userId}")
    @Operation(summary = "Désarchiver une conversation")
    public ResponseEntity<Void> desarchiverConversation(
            @PathVariable Long conversationId,
            @PathVariable Long userId) {
        messageriePriveeService.desarchiverConversation(conversationId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/non-lus/user/{userId}")
    @Operation(summary = "Récupérer tous les messages non lus d'un utilisateur")
    public ResponseEntity<List<MessageDto>> getMessagesNonLus(@PathVariable Long userId) {
        List<MessageDto> messages = messageriePriveeService.getMessagesNonLus(userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/non-lus/count/user/{userId}")
    @Operation(summary = "Récupérer le nombre de messages non lus d'un utilisateur")
    public ResponseEntity<Integer> getNombreMessagesNonLus(@PathVariable Long userId) {
        Integer count = messageriePriveeService.getNombreMessagesNonLus(userId);
        return ResponseEntity.ok(count);
    }

    @DeleteMapping("/{messageId}/user/{userId}")
    @Operation(summary = "Supprimer un message (seul l'expéditeur peut supprimer)")
    public ResponseEntity<Void> supprimerMessage(
            @PathVariable Long messageId,
            @PathVariable Long userId) {
        messageriePriveeService.supprimerMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    // ========== MESSAGERIE DE GROUPE ==========

    @PostMapping("/groupes")
    @Operation(summary = "Créer un nouveau groupe de discussion")
    public ResponseEntity<GroupeMessageDto> creerGroupe(@Valid @RequestBody CreateGroupeRequest request) {
        GroupeMessageDto groupe = messagerieGroupeService.creerGroupe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupe);
    }

    @PostMapping("/groupes/message")
    @Operation(summary = "Envoyer un message dans un groupe")
    public ResponseEntity<MessageDto> envoyerMessageGroupe(@Valid @RequestBody SendMessageRequest request) {
        MessageDto message = messagerieGroupeService.envoyerMessageGroupe(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/groupes/user/{userId}")
    @Operation(summary = "Récupérer tous les groupes d'un utilisateur")
    public ResponseEntity<List<GroupeMessageDto>> getGroupesByUser(@PathVariable Long userId) {
        List<GroupeMessageDto> groupes = messagerieGroupeService.getGroupesByUser(userId);
        return ResponseEntity.ok(groupes);
    }

    @GetMapping("/groupes/matiere/{matiereId}")
    @Operation(summary = "Récupérer tous les groupes d'une matière")
    public ResponseEntity<List<GroupeMessageDto>> getGroupesByMatiere(@PathVariable Long matiereId) {
        List<GroupeMessageDto> groupes = messagerieGroupeService.getGroupesByMatiere(matiereId);
        return ResponseEntity.ok(groupes);
    }

    @GetMapping("/groupes/cours/{coursId}")
    @Operation(summary = "Récupérer tous les groupes d'un cours")
    public ResponseEntity<List<GroupeMessageDto>> getGroupesByCours(@PathVariable Long coursId) {
        List<GroupeMessageDto> groupes = messagerieGroupeService.getGroupesByCours(coursId);
        return ResponseEntity.ok(groupes);
    }

    @GetMapping("/groupes/{groupeId}/user/{userId}")
    @Operation(summary = "Récupérer un groupe spécifique")
    public ResponseEntity<GroupeMessageDto> getGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId) {
        GroupeMessageDto groupe = messagerieGroupeService.getGroupe(groupeId, userId);
        return ResponseEntity.ok(groupe);
    }

    @GetMapping("/groupes/{groupeId}/messages/user/{userId}")
    @Operation(summary = "Récupérer tous les messages d'un groupe")
    public ResponseEntity<List<MessageDto>> getMessagesGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId) {
        List<MessageDto> messages = messagerieGroupeService.getMessagesGroupe(groupeId, userId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/groupes/{groupeId}/membres/user/{userId}")
    @Operation(summary = "Ajouter des membres à un groupe")
    public ResponseEntity<Void> ajouterMembres(
            @PathVariable Long groupeId,
            @PathVariable Long userId,
            @RequestBody Set<Long> membresIds) {
        messagerieGroupeService.ajouterMembres(groupeId, membresIds, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/groupes/{groupeId}/membres/{membreId}/user/{userId}")
    @Operation(summary = "Retirer un membre d'un groupe")
    public ResponseEntity<Void> retirerMembre(
            @PathVariable Long groupeId,
            @PathVariable Long membreId,
            @PathVariable Long userId) {
        messagerieGroupeService.retirerMembre(groupeId, membreId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/groupes/{groupeId}/desactiver/user/{userId}")
    @Operation(summary = "Désactiver un groupe (seul le créateur)")
    public ResponseEntity<Void> desactiverGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId) {
        messagerieGroupeService.desactiverGroupe(groupeId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/groupes/{groupeId}/activer/user/{userId}")
    @Operation(summary = "Activer un groupe (seul le créateur)")
    public ResponseEntity<Void> activerGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId) {
        messagerieGroupeService.activerGroupe(groupeId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/groupes/{groupeId}/user/{userId}")
    @Operation(summary = "Modifier un groupe (nom et description)")
    public ResponseEntity<Void> modifierGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String description) {
        messagerieGroupeService.modifierGroupe(groupeId, nom, description, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/groupes/{groupeId}/user/{userId}")
    @Operation(summary = "Supprimer un groupe (seul le créateur)")
    public ResponseEntity<Void> supprimerGroupe(
            @PathVariable Long groupeId,
            @PathVariable Long userId) {
        messagerieGroupeService.supprimerGroupe(groupeId, userId);
        return ResponseEntity.noContent().build();
    }
}
