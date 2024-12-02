package service.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Ingredients {
    private List<String> ingredients;

    public Ingredients() {
        ingredients = new ArrayList<>();
    }

    public void addIngredient(String ingredient) {
        ingredients.add(ingredient);
    }
}
