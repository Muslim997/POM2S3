package com.elzocodeur.campusmaster.application.service;

import com.elzocodeur.campusmaster.application.dto.notification.NotificationPreferenceDto;
import com.elzocodeur.campusmaster.domain.entity.NotificationPreference;
import com.elzocodeur.campusmaster.domain.entity.User;
import com.elzocodeur.campusmaster.domain.enums.NotificationChannel;
import com.elzocodeur.campusmaster.domain.enums.NotificationEvent;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.NotificationPreferenceRepository;
import com.elzocodeur.campusmaster.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<NotificationPreferenceDto> getUserPreferences(Long userId) {
        return preferenceRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationPreferenceDto updatePreference(
            Long userId,
            NotificationEvent eventType,
            NotificationChannel channel,
            Boolean enabled
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        NotificationPreference preference = preferenceRepository
                .findByUserIdAndEventTypeAndChannel(userId, eventType, channel)
                .orElseGet(() -> NotificationPreference.builder()
                        .user(user)
                        .eventType(eventType)
                        .channel(channel)
                        .build());

        preference.setEnabled(enabled);
        preference = preferenceRepository.save(preference);

        return toDto(preference);
    }

    @Transactional
    public NotificationPreferenceDto togglePreference(Long preferenceId) {
        NotificationPreference preference = preferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new RuntimeException("Préférence introuvable"));

        preference.setEnabled(!preference.getEnabled());
        preference = preferenceRepository.save(preference);

        return toDto(preference);
    }

    @Transactional
    public void deletePreference(Long preferenceId) {
        preferenceRepository.deleteById(preferenceId);
    }

    @Transactional
    public List<NotificationPreferenceDto> createDefaultPreferences(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        List<NotificationPreference> preferences = new ArrayList<>();

        // Créer des préférences par défaut pour chaque événement et canal
        NotificationEvent[] events = {
                NotificationEvent.DEVOIR_PUBLIE,
                NotificationEvent.DEVOIR_CORRIGE,
                NotificationEvent.NOUVEAU_MESSAGE,
                NotificationEvent.DEADLINE_PROCHE,
                NotificationEvent.NOUVELLE_ANNONCE
        };

        for (NotificationEvent event : events) {
            // Par défaut, activer EMAIL et WEB_PUSH pour les événements importants
            if (event == NotificationEvent.DEVOIR_PUBLIE ||
                event == NotificationEvent.DEADLINE_PROCHE ||
                event == NotificationEvent.NOUVELLE_ANNONCE) {

                preferences.add(createPreference(user, event, NotificationChannel.EMAIL, true));
                preferences.add(createPreference(user, event, NotificationChannel.WEB_PUSH, true));
                preferences.add(createPreference(user, event, NotificationChannel.IN_APP, true));

            } else {
                // Pour les autres événements, activer seulement WEB_PUSH et IN_APP
                preferences.add(createPreference(user, event, NotificationChannel.EMAIL, false));
                preferences.add(createPreference(user, event, NotificationChannel.WEB_PUSH, true));
                preferences.add(createPreference(user, event, NotificationChannel.IN_APP, true));
            }
        }

        preferences = preferenceRepository.saveAll(preferences);

        return preferences.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private NotificationPreference createPreference(
            User user,
            NotificationEvent event,
            NotificationChannel channel,
            Boolean enabled
    ) {
        return NotificationPreference.builder()
                .user(user)
                .eventType(event)
                .channel(channel)
                .enabled(enabled)
                .build();
    }

    private NotificationPreferenceDto toDto(NotificationPreference preference) {
        return NotificationPreferenceDto.builder()
                .id(preference.getId())
                .userId(preference.getUser().getId())
                .eventType(preference.getEventType().name())
                .channel(preference.getChannel().name())
                .enabled(preference.getEnabled())
                .build();
    }
}
