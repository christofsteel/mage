/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */
package mage.cards.w;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.AttacksAllTriggeredAbility;
import mage.abilities.common.TapForManaAllTriggeredAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.TapAllEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SetTargetPointer;
import mage.constants.TargetController;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.common.FilterLandPermanent;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.filter.predicate.permanent.ControllerPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author jeffwadsworth
 */
public class WarsToll extends CardImpl {

    private final static FilterCreaturePermanent filterOpponentCreature = new FilterCreaturePermanent("creature an opponent controls");
    private final static FilterLandPermanent filterOpponentLand = new FilterLandPermanent("an opponent taps a land");

    static {
        filterOpponentCreature.add(new ControllerPredicate(TargetController.OPPONENT));
        filterOpponentLand.add(new ControllerPredicate(TargetController.OPPONENT));
    }

    public WarsToll(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{3}{R}");

        // Whenever an opponent taps a land for mana, tap all lands that player controls.
        Effect effect = new TapAllEffect(filterOpponentLand);
        effect.setText("tap all lands that player controls");
        this.addAbility(new TapForManaAllTriggeredAbility(effect, filterOpponentLand, SetTargetPointer.PLAYER));

        // If a creature an opponent controls attacks, all creatures that opponent controls attack if able.
        this.addAbility(new AttacksAllTriggeredAbility(new WarsTollEffect(), false, filterOpponentCreature, SetTargetPointer.PERMANENT, true));

    }

    public WarsToll(final WarsToll card) {
        super(card);
    }

    @Override
    public WarsToll copy() {
        return new WarsToll(this);
    }
}

class TapAlEffect extends OneShotEffect {

    protected FilterPermanent filter;

    public TapAlEffect(FilterPermanent filter) {
        super(Outcome.Tap);
        this.filter = filter;
        setText();
    }

    public TapAlEffect(final TapAlEffect effect) {
        super(effect);
        this.filter = effect.filter.copy();
    }

    @Override
    public TapAlEffect copy() {
        return new TapAlEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        for (Permanent permanent : game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source.getSourceId(), game)) {
            if (permanent.getControllerId().equals(source.getFirstTarget())) {
                permanent.tap(game);
            }
        }
        return true;
    }

    private void setText() {
        staticText = "tap all " + filter.getMessage();
    }

}

class WarsTollEffect extends OneShotEffect {

    private static final FilterCreaturePermanent filterOpponentCreatures = new FilterCreaturePermanent();

    public WarsTollEffect() {
        super(Outcome.Neutral);
        staticText = "all creatures that opponent controls attack if able";
    }

    public WarsTollEffect(final WarsTollEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player opponent = game.getPlayer(game.getPermanent(getTargetPointer().getFirst(game, source)).getControllerId());
        if (opponent != null) {
            filterOpponentCreatures.add(new ControllerIdPredicate(opponent.getId()));
            for (Permanent permanent : game.getBattlefield().getAllActivePermanents(CardType.CREATURE)) {
                if (filterOpponentCreatures.match(permanent, source.getSourceId(), source.getControllerId(), game)) {
                    //TODO: allow the player to choose between a planeswalker and player
                    opponent.declareAttacker(permanent.getId(), source.getControllerId(), game, false);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public WarsTollEffect copy() {
        return new WarsTollEffect(this);
    }
}
