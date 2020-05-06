package com.lalitadithya;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class CommandController {

    @Value("#{environment.MY_POD_NAME}")
    private String podName;

    @Autowired
    HazelcastInstance hazelcastInstance;

    private Map<String, Seat> retrieveMap() {
        return hazelcastInstance.getMap("seat-matrix");
    }

    private FencedLock getLock(String name) {
        return hazelcastInstance.getCPSubsystem().getLock(name);
    }

    @RequestMapping("/reserveSeat")
    public boolean reserveSeat(@RequestParam(value = "seatNumber") String seatNumber,
                               @RequestParam(value = "clientName") String clientName) {
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(seatNumber);
        lock.lock();
        try {
            Seat seat = retrieveMap().get(seatNumber);
            if (seat != null && !seat.isBooked()) {
                long aLong = System.currentTimeMillis() - seat.getLastHeartbeat();
                if (aLong < 30000) {
                    return false;
                } else {
                    retrieveMap().put(seatNumber, new Seat(clientName, System.currentTimeMillis(), false));
                    return true;
                }
            } else {
                retrieveMap().put(seatNumber, new Seat(clientName, System.currentTimeMillis(), false));
                return true;
            }
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping("/updateHeartbeat")
    public boolean updateHeartbeat(@RequestParam(value = "seatNumber") String seatNumber,
                                   @RequestParam(value = "clientName") String clientName) {
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(seatNumber);
        lock.lock();
        try {
            Seat seat = retrieveMap().get(seatNumber);
            if (seat != null && seat.getClientName().equals(clientName)) {
                seat.setLastHeartbeat(System.currentTimeMillis());
                retrieveMap().put(seatNumber, seat);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping("/completeBooking")
    public boolean completeBooking(@RequestParam(value = "seatNumber") String seatNumber,
                                   @RequestParam(value = "clientName") String clientName) {
        FencedLock lock = hazelcastInstance.getCPSubsystem().getLock(seatNumber);
        lock.lock();
        try {
            Seat seat = retrieveMap().get(seatNumber);
            if (seat != null && seat.getClientName().equals(clientName)) {
                seat.setBooked(true);
                retrieveMap().put(seatNumber, seat);
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    @RequestMapping("/getMap")
    public List<Seat> getMap() {
        return new ArrayList<>(retrieveMap().values());
    }

    @RequestMapping("/put")
    public CommandResponse put(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {
        //retrieveMap().put(key, value);
        return new CommandResponse(value, podName);
    }

    @RequestMapping("/get")
    public CommandResponse get(@RequestParam(value = "key") String key) {
        FencedLock lock = getLock(key);
        long fence = lock.tryLockAndGetFence();

        return new CommandResponse(lock.getFence() + "", podName);
    }


}