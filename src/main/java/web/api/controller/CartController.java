package web.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import web.api.domain.AggregateRoot;
import web.api.eventSourcing.query.Cart;
import web.api.eventSourcing.command.CartCommand;
import web.api.eventSourcing.snapshot.Snapshot;
import web.api.service.CartService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @RequestMapping(value = "/carts", method = RequestMethod.POST)
    public ResponseEntity<Long> createOrder(@RequestBody @Validated CartCommand.CreateCart cartCreatedCommand, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception("Invalid Parameter!");
        }

        final Cart cart = cartService.createCart(cartCreatedCommand);

        return new ResponseEntity<>(cart.getMemberId(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/carts/{memberId}", method = RequestMethod.GET)
    public ResponseEntity findAll(@PathVariable Long memberId) throws Exception {
        AggregateRoot cart = cartService.findCartByMemberId(memberId);
        return new ResponseEntity(cart, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/carts/snapshot", method = RequestMethod.GET)
    public ResponseEntity findAllSnapshot() {
        List<Snapshot> snapshots = cartService.findAllSnapshot();
        return new ResponseEntity(snapshots, HttpStatus.CREATED);
    }

}
