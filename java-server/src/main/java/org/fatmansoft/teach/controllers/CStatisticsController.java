package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.models.Clocking;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.CStatistics;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ClockingRepository;
import org.fatmansoft.teach.repository.PersonRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.repository.CStatisticsRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cStatistics")


public class CStatisticsController {
    @Autowired
    private ClockingRepository clockingRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CStatisticsRepository cStatisticsRepository;
    @PostMapping("/cStatisticsSave")
    @PreAuthorize(" hasRole('ADMIN')")
    public DataResponse clStatisticsSave(@Valid @RequestBody DataRequest dataRequest) {
        return null;
    }
}
