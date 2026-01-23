package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.notification.NotificationDto;
import com.elzocodeur.campusmaster.domain.entity.*;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.InscriptionRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.NotificationRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UserRepository userRepository;

    public List<NotificationDto> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndEstLu(userId, false).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setEstLu(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndEstLu(userId, false);
        notifications.forEach(n -> n.setEstLu(true));
        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void notifierNouveauSupport(Cours cours, Support support) {
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());

        for (Inscription inscription : inscriptions) {
            User etudiantUser = inscription.getEtudiant().getUser();
            createNotification(
                etudiantUser,
                "Nouveau support ajouté",
                String.format("Un nouveau support '%s' a été ajouté au cours '%s'",
                    support.getTitre(), cours.getTitre())
            );
        }
    }

    @Transactional
    public void notifierNouveauDevoir(Cours cours, Devoir devoir) {
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());

        for (Inscription inscription : inscriptions) {
            User etudiantUser = inscription.getEtudiant().getUser();
            createNotification(
                etudiantUser,
                "Nouveau devoir assigné",
                String.format("Un nouveau devoir '%s' a été assigné pour le cours '%s'. Date limite: %s",
                    devoir.getTitre(), cours.getTitre(), devoir.getDateLimite())
            );
        }
    }

    @Transactional
    public void notifierNotePubliee(Submit submit) {
        User etudiantUser = submit.getEtudiant().getUser();
        createNotification(
            etudiantUser,
            "Note publiée",
            String.format("Votre note pour le devoir '%s' a été publiée: %.2f/20",
                submit.getDevoir().getTitre(), submit.getNote())
        );
    }

    @Transactional
    public void notifierDeadlineApproche(Devoir devoir, Etudiant etudiant) {
        createNotification(
            etudiant.getUser(),
            "Deadline approche",
            String.format("Le devoir '%s' doit être rendu avant le %s",
                devoir.getTitre(), devoir.getDateLimite())
        );
    }

    @Transactional
    public void notifierNouvelleAnnonce(Cours cours, Annonce annonce) {
        List<Inscription> inscriptions = inscriptionRepository.findByCoursId(cours.getId());

        for (Inscription inscription : inscriptions) {
            User etudiantUser = inscription.getEtudiant().getUser();
            createNotification(
                etudiantUser,
                "Nouvelle annonce: " + annonce.getTitre(),
                String.format("Une nouvelle annonce a été publiée pour le cours '%s': %s",
                    cours.getTitre(), annonce.getContenu())
            );
        }
    }

    @Transactional
    public NotificationDto createNotification(User user, String titre, String contenu) {
        Notification notification = Notification.builder()
                .titre(titre)
                .contenu(contenu)
                .estLu(false)
                .dateEnvoi(LocalDateTime.now())
                .user(user)
                .build();

        notification = notificationRepository.save(notification);
        return toDto(notification);
    }

    private NotificationDto toDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .titre(notification.getTitre())
                .contenu(notification.getContenu())
                .estLu(notification.getEstLu())
                .dateEnvoi(notification.getDateEnvoi())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
