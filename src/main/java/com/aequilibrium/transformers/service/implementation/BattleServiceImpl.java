package com.aequilibrium.transformers.service.implementation;

import com.aequilibrium.transformers.domain.*;
import com.aequilibrium.transformers.dto.BattleRequest;
import com.aequilibrium.transformers.service.BattleService;
import com.aequilibrium.transformers.service.TransformerService;
import com.aequilibrium.transformers.service.TransformsComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class BattleServiceImpl implements BattleService {
    private Team autobots;
    private Team decepticons;
    private final List<TransformerBattle> battles = new ArrayList<>();
    private final TransformsComparator comparator;
    private final TransformerService transformerService;

    @Autowired
    public BattleServiceImpl(TransformsComparator comparator, TransformerService transformerService) {
        this.comparator = comparator;
        this.transformerService = transformerService;
    }

    @Override
    public BattleResult fight(BattleRequest request) {
        setup(request);
        IntStream.range(0, Math.min(autobots.getMembers().size(), decepticons.getMembers().size()))
                .forEach(index -> {
                    Transformer autobot = autobots.getMembers().get(index);
                    Transformer decepticon = decepticons.getMembers().get(index);
                    BattleStatus status = comparator.compare(autobot, decepticon);
                    updateStatus(autobot, decepticon, status);
                });
        return buildResult();
    }

    private void setup(BattleRequest request) {
        autobots = Team.autobots(request.getAutobots().getName(), getMembers(request.getAutobots().getMembers()));
        decepticons = Team.decepticons(request.getDecepticons().getName(), getMembers(request.getDecepticons().getMembers()));
    }

    private List<Transformer> getMembers(List<Long> ids) {
        return ids.stream()
                .map(transformerService::get)
                .sorted((o1, o2) -> o2.getRank().compareTo(o1.getRank()))
                .collect(Collectors.toList());
    }

    public void updateStatus(Transformer autobot, Transformer decepticon, BattleStatus status) {
        battles.add(new TransformerBattle(autobot, decepticon, status));
    }

    private BattleResult buildResult() {
        List<TransformerBattle> autobotsWins = filterBattles(BattleStatus.AUTOBOT);
        List<TransformerBattle> deceptionsWins = filterBattles(BattleStatus.DECEPTICON);
        if (autobotsWins.size() > deceptionsWins.size()) {
            return new BattleResult(battles.size(), autobots,
                    deceptionsWins.stream().map(TransformerBattle::getDecepticon).collect(Collectors.toList())
            );
        } else if (deceptionsWins.size() > autobotsWins.size()) {
            return new BattleResult(battles.size(), decepticons,
                    autobotsWins.stream().map(TransformerBattle::getDecepticon).collect(Collectors.toList())
            );
        }
        return BattleResult.tie(deceptionsWins.size());
    }

    private List<TransformerBattle> filterBattles(BattleStatus status) {
        return battles.stream()
                .filter(battle -> battle.getStatus().equals(status))
                .collect(Collectors.toList());
    }
}