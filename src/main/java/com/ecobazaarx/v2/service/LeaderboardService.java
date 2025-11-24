package com.ecobazaarx.v2.service;

import com.ecobazaarx.v2.dto.LeaderboardEntryDto;
import com.ecobazaarx.v2.model.User;
import com.ecobazaarx.v2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final UserRepository userRepository;

    // --- FIXED: Carbon Efficiency Logic ---
    // 1. Average Carbon (Low to High) - Efficiency is key
    // 2. Rank Level (High to Low) - Tie-breaker for activity
    private final Sort leaderboardSort = Sort.by(
            Sort.Order.asc("lifetimeAverageCarbon"),
            Sort.Order.desc("rankLevel")
    );

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getGlobalLeaderboard() {
        Pageable top100 = PageRequest.of(0, 100, leaderboardSort);
        // Use the pure customer filter (Active Customers Only)
        Page<User> userPage = userRepository.findPureCustomers(top100);
        return mapToDtoList(userPage);
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getLeaderboardByLevel(int level) {
        Pageable top100 = PageRequest.of(0, 100, leaderboardSort);
        Page<User> userPage = userRepository.findByRankLevel(level, top100);
        return mapToDtoList(userPage);
    }

    private List<LeaderboardEntryDto> mapToDtoList(Page<User> userPage) {
        List<User> users = userPage.getContent();

        return IntStream.range(0, users.size())
                .mapToObj(i -> {
                    User user = users.get(i);
                    return LeaderboardEntryDto.builder()
                            .rank(i + 1 + (userPage.getNumber() * userPage.getSize()))
                            .userName(user.getName())
                            .rankLevel(user.getRankLevel())
                            .averageCarbonFootprint(user.getLifetimeAverageCarbon())
                            .ecoPoints(user.getEcoPoints())
                            .build();
                })
                .collect(Collectors.toList());
    }
}