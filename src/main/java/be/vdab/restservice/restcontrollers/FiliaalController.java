package be.vdab.restservice.restcontrollers;

import be.vdab.restservice.domain.Filiaal;
import be.vdab.restservice.exceptions.FiliaalNietGevondenException;
import be.vdab.restservice.services.FiliaalService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.hateoas.server.TypedEntityLinks;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/filialen")
@ExposesResourceFor(Filiaal.class)
@CrossOrigin(exposedHeaders = "Location")
class FiliaalController {
    private final FiliaalService filiaalService;
    private final TypedEntityLinks.ExtendedTypedEntityLinks<Filiaal> links;

    FiliaalController(FiliaalService filiaalService, EntityLinks links) {
        this.filiaalService = filiaalService;
        this.links = links.forType(Filiaal.class, Filiaal::getId);
    }

    @GetMapping
    @Operation(summary = "Alle filialen zoeken")
    CollectionModel<EntityModel<FiliaalIdNaam>> findAll() {
        return CollectionModel.of(
                filiaalService.findAll().stream()
                        .map(filiaal ->
                                EntityModel.of(new FiliaalIdNaam(filiaal),
                                        links.linkToItemResource(filiaal)))
                        ::iterator,
                links.linkToCollectionResource());
    }

    @GetMapping("{id}")
    @Operation(summary = "Een filiaal zoeken op id")
    EntityModel<Filiaal> get(@PathVariable long id) {
        return filiaalService.findById(id)
                .map(filiaal -> EntityModel.of(filiaal,
                        links.linkToItemResource(filiaal),
                        links.linkForItemResource(filiaal)
                                .slash("werknemers").withRel("werknemers")))
                .orElseThrow(FiliaalNietGevondenException::new);
    }


    @ExceptionHandler(FiliaalNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void filiaalNietGevonden() {
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Een filiaal verwijderen")
    void delete(@PathVariable long id) {
        filiaalService.delete(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Een filiaal toevoegen")
    HttpHeaders create(@RequestBody @Valid Filiaal filiaal) {
        filiaalService.create(filiaal);
        var headers = new HttpHeaders();
        headers.setLocation(links.linkToItemResource(filiaal).toUri());
        return headers;
    }
/*    void post(@RequestBody @Valid Filiaal filiaal) {
        filiaalService.create(filiaal);
    }*/

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String, String> verkeerdeData(MethodArgumentNotValidException ex) {
        return ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    @PutMapping("{id}")
    @Operation(summary = "Een filiaal wijzigen")
    void put(@PathVariable long id, @RequestBody @Valid Filiaal filiaal) {
        if (filiaalService.findById(id).isEmpty()) {
            throw new FiliaalNietGevondenException();
        }
        filiaalService.update(filiaal.withId(id));
    }

    private record FiliaalIdNaam(long id, String naam) {
        FiliaalIdNaam(Filiaal filiaal) {
            this(filiaal.getId(), filiaal.getNaam());
        }
    }
}
