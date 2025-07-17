package learning.journalAPP.service;

import learning.journalAPP.entity.JournalEntry;
import learning.journalAPP.entity.User;
import learning.journalAPP.repository.JournalEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;


    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){
        try {
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntryList().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            throw new RuntimeException("error occured:",e);
        }
    }

    public void saveEntry(JournalEntry journalEntry){
        journalEntryRepository.save(journalEntry);
    }


    public List<JournalEntry> getAll(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findByid(ObjectId id){
        return journalEntryRepository.findById(id);
    }

    public boolean deleteByid(ObjectId id, String userName){
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
             removed = user.getJournalEntryList().removeIf(x -> x.getId().equals(id));
            if (removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(id);
            }
        } catch (Exception e) {
            log.error("Error", e);
            throw new RuntimeException("error occured while deleting user",e);
        }
        return removed;
    }
}
