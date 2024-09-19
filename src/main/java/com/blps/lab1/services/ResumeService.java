package com.blps.lab1.services;

import com.blps.lab1.exceptions.NoEntitiesException;
import com.blps.lab1.model.common.Status;
import com.blps.lab1.model.emailmessage.EmailMessage;
import com.blps.lab1.model.cv.Resume;
import com.blps.lab1.repositories.ResumeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final EmailService emailService;
    private final UserService userService;

    public Resume create(Resume.ResumeBuilder resume, Long userId) {
        resume.createdBy(userId);
        Long time = System.currentTimeMillis();
        resume.createdAt(time);
        resume.updatedAt(time);
        var buildedResume = resume.build();
        resumeRepository.save(buildedResume);
        return buildedResume;
    }

    @Transactional
    public Resume getResumeForReview(Long moderatorId) {
        var assignedResume = resumeRepository.findByModeratorIdAndStatus(moderatorId, Status.ASSIGNED);
        if (assignedResume.isPresent()) return assignedResume.get();

        var resume = resumeRepository.findOldestWaiting().orElseThrow(() -> new NoEntitiesException("Новых резюме для проверки пока нет"));
        System.out.println("lol");
        resume.setStatus(Status.ASSIGNED);
        resume.setModeratorId(moderatorId);
        resume.setUpdatedAt(System.currentTimeMillis());
        System.out.println("kek");
        resumeRepository.save(resume);
        return resume;
    }

    public Resume getByUserId(Long userId) {
        return resumeRepository.findByCreatedBy(userId).orElseThrow(() -> new NoEntitiesException("Вы еще не создали резюме"));
    }

    public Collection<Resume> getAllByUserIds(Collection<Long> userIds) {
        return resumeRepository.findAllById(userIds);
    }

    @Transactional
    public void validate(Long resumeId, Boolean isValid) {
        var resume = resumeRepository.findById(resumeId).orElseThrow();

        Status resultStatus = isValid ? Status.APPROVED : Status.ASSIGNED;

        resume.setStatus(resultStatus);
        resume.setUpdatedAt(System.currentTimeMillis());
        resumeRepository.save(resume);

        var userEmail = userService.findById(resume.getCreatedBy()).getEmail();
    }
}
