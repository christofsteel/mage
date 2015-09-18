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
package mage.sets.battleforzendikar;

import java.util.UUID;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ReturnToHandTargetEffect;
import mage.abilities.keyword.DevoidAbility;
import mage.cards.CardImpl;
import mage.cards.Cards;
import mage.cards.CardsImpl;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.filter.FilterCard;
import mage.filter.predicate.other.OwnerPredicate;
import mage.game.Game;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetCardInExile;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author fireshoes
 */
public class MurkStrider extends CardImpl {

    public MurkStrider(UUID ownerId) {
        super(ownerId, 62, "Murk Strider", Rarity.COMMON, new CardType[]{CardType.CREATURE}, "{3}{U}");
        this.expansionSetCode = "BFZ";
        this.subtype.add("Eldrazi");
        this.subtype.add("Processor");
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        // Devoid
        this.addAbility(new DevoidAbility(this.color));

        // When Murk Strider enters the battlefield, you may put a card an opponent owns from exile into that player's graveyard. If you do, return target creature to its owner's hand.
        Ability ability = new EntersBattlefieldTriggeredAbility(new MurkStriderEffect(), true);
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
    }

    public MurkStrider(final MurkStrider card) {
        super(card);
    }

    @Override
    public MurkStrider copy() {
        return new MurkStrider(this);
    }
}

class MurkStriderEffect extends OneShotEffect {

    private final static FilterCard filter = new FilterCard("card an opponent owns from exile");

    static {
        filter.add(new OwnerPredicate(TargetController.OPPONENT));
    }

    public MurkStriderEffect() {
        super(Outcome.Discard);
        this.staticText = "you may put a card an opponent owns from exile into that player's graveyard. If you do, return target creature to its owner's hand.";
    }

    public MurkStriderEffect(final MurkStriderEffect effect) {
        super(effect);
    }

    @Override
    public MurkStriderEffect copy() {
        return new MurkStriderEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null) {
            Target target = new TargetCardInExile(1, 1, filter, null);
            if (target.canChoose(source.getSourceId(), source.getControllerId(), game)) {
                if (controller.chooseTarget(outcome, target, source, game)) {
                    Cards cardsToGraveyard = new CardsImpl(target.getTargets());
                    controller.moveCards(cardsToGraveyard, null, Zone.GRAVEYARD, source, game);
                    return new ReturnToHandTargetEffect().apply(game, source);
                }
            }
            return true;
        }
        return false;
    }
}