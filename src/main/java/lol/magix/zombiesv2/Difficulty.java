package lol.magix.zombiesv2;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.Pair;

/**
 * Holds bindings for difficulty -> ...
 */
@Getter
@AllArgsConstructor
public enum Difficulty {
    D1(5, 0, 0, 0, new Pair<>(0, 0), 5, 1, 0, new Pair<>(0, 1), 0, new Pair<>(0, 0), 0, 0),
    D2(10, 0, 0, 0, new Pair<>(0, 0), 10, 2, 0, new Pair<>(0, 1), 0, new Pair<>(0, 0), 0, 0),
    D3(15, 0, 0, 0, new Pair<>(0, 0), 15, 3, 0, new Pair<>(0, 1), 0, new Pair<>(0, 0), 0, 0),
    D4(20, 20, 5, 20, new Pair<>(0, 1), 25, 1, 0, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D5(30, 40, 10, 20, new Pair<>(0, 1), 30, 1, 0, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D6(40, 60, 20, 15, new Pair<>(0, 2), 40, 2, 0, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D7(50, 80, 35, 15, new Pair<>(0, 2), 50, 2, 0, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D8(70, 100, 55, 40, new Pair<>(1, 2), 60, 2, 10, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D9(100, 100, 90, 60, new Pair<>(1, 2), 80, 2, 30, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D10(100, 100, 100, 80, new Pair<>(2, 2), 100, 2, 50, new Pair<>(0, 2), 0, new Pair<>(0, 0), 0, 0),
    D11(100, 5, 30, 25, new Pair<>(0, 1), 20, 1, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D12(100, 10, 25, 25, new Pair<>(0, 1), 20, 1, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D13(100, 15, 20, 25, new Pair<>(0, 2), 20, 1, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D14(100, 20, 15, 25, new Pair<>(0, 2), 30, 2, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D15(100, 25, 10, 25, new Pair<>(0, 3), 30, 2, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D16(100, 30, 10, 25, new Pair<>(0, 3), 30, 2, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D17(100, 35, 10, 25, new Pair<>(0, 4), 40, 3, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D18(100, 40, 20, 50, new Pair<>(0, 4), 40, 3, 0, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D19(100, 45, 20, 50, new Pair<>(1, 2), 40, 3, 10, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D20(100, 50, 20, 50, new Pair<>(1, 2), 50, 4, 10, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D21(100, 55, 20, 50, new Pair<>(1, 2), 50, 4, 10, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D22(100, 60, 20, 50, new Pair<>(1, 3), 50, 4, 10, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D23(100, 65, 30, 50, new Pair<>(1, 3), 60, 3, 10, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D24(100, 70, 30, 50, new Pair<>(1, 3), 60, 3, 30, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D25(100, 75, 30, 75, new Pair<>(1, 4), 60, 3, 30, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D26(100, 80, 30, 75, new Pair<>(1, 5), 70, 3, 30, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D27(100, 85, 20, 75, new Pair<>(2, 3), 70, 3, 30, new Pair<>(0, 3), 0, new Pair<>(0, 0), 0, 0),
    D28(100, 90, 20, 75, new Pair<>(2, 3), 70, 3, 30, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D29(100, 95, 20, 75, new Pair<>(2, 3), 80, 3, 30, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D30(100, 100, 20, 75, new Pair<>(2, 4), 80, 4, 30, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D31(100, 100, 20, 75, new Pair<>(2, 4), 80, 4, 30, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D32(100, 100, 30, 75, new Pair<>(2, 4), 90, 4, 30, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D33(100, 100, 40, 100, new Pair<>(3, 4), 90, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D34(100, 100, 50, 100, new Pair<>(3, 4), 90, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D35(100, 100, 60, 100, new Pair<>(3, 4), 100, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D36(100, 100, 70, 70, new Pair<>(3, 4), 100, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D37(100, 100, 80, 80, new Pair<>(4, 4), 100, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D38(100, 100, 90, 90, new Pair<>(4, 4), 85, 5, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D39(100, 100, 100, 100, new Pair<>(4, 4), 85, 5, 80, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D40(100, 100, 100, 100, new Pair<>(4, 4), 85, 5, 80, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D41(100, 5, 30, 25, new Pair<>(4, 6), 20, 1, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D42(100, 10, 25, 25, new Pair<>(4, 6), 20, 1, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D43(100, 15, 20, 25, new Pair<>(4, 6), 20, 1, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D44(100, 20, 15, 25, new Pair<>(4, 6), 30, 2, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D45(100, 25, 10, 25, new Pair<>(4, 6), 30, 2, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D46(100, 30, 10, 25, new Pair<>(4, 6), 30, 2, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D47(100, 35, 10, 25, new Pair<>(4, 6), 40, 3, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D48(100, 40, 20, 50, new Pair<>(4, 6), 40, 3, 20, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D49(100, 45, 20, 50, new Pair<>(4, 6), 40, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D50(100, 50, 20, 50, new Pair<>(4, 6), 50, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D51(100, 55, 20, 50, new Pair<>(4, 6), 50, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D52(100, 60, 20, 50, new Pair<>(5, 8), 50, 4, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D53(100, 65, 30, 50, new Pair<>(5, 8), 60, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D54(100, 70, 30, 50, new Pair<>(5, 8), 60, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D55(100, 75, 30, 75, new Pair<>(5, 8), 60, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D56(100, 80, 30, 75, new Pair<>(5, 8), 70, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D57(100, 85, 20, 75, new Pair<>(5, 8), 70, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D58(100, 90, 20, 75, new Pair<>(5, 8), 70, 3, 50, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D59(100, 95, 20, 75, new Pair<>(5, 8), 80, 3, 70, new Pair<>(1, 3), 0, new Pair<>(0, 0), 0, 0),
    D60(100, 100, 20, 75, new Pair<>(5, 8), 80, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D61(100, 100, 20, 75, new Pair<>(5, 8), 80, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D62(100, 100, 30, 75, new Pair<>(5, 8), 90, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D63(100, 100, 40, 100, new Pair<>(5, 8), 90, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D64(100, 100, 50, 100, new Pair<>(6, 9), 90, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D65(100, 100, 60, 100, new Pair<>(6, 9), 100, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D66(100, 100, 70, 70, new Pair<>(6, 9), 100, 4, 70, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D67(100, 100, 80, 80, new Pair<>(6, 9), 100, 4, 100, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D68(100, 100, 90, 90, new Pair<>(6, 9), 85, 5, 100, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D69(100, 100, 100, 100, new Pair<>(6, 9), 85, 5, 100, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D70(100, 100, 100, 100, new Pair<>(6, 9), 85, 5, 100, new Pair<>(2, 3), 0, new Pair<>(0, 0), 0, 0),
    D71(100, 5, 30, 25, new Pair<>(6, 9), 20, 1, 10, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D72(100, 10, 25, 25, new Pair<>(6, 9), 20, 1, 10, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D73(100, 15, 20, 25, new Pair<>(6, 9), 20, 1, 10, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D74(100, 20, 15, 25, new Pair<>(6, 9), 30, 2, 10, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D75(100, 25, 10, 25, new Pair<>(6, 9), 30, 2, 10, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D76(100, 30, 10, 25, new Pair<>(6, 9), 30, 2, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D77(100, 35, 10, 25, new Pair<>(7, 9), 40, 3, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D78(100, 40, 20, 50, new Pair<>(7, 9), 40, 3, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D79(100, 45, 20, 50, new Pair<>(7, 9), 40, 3, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D80(100, 50, 20, 50, new Pair<>(7, 9), 50, 4, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D81(100, 55, 20, 50, new Pair<>(7, 9), 50, 4, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D82(100, 60, 20, 50, new Pair<>(7, 9), 50, 4, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D83(100, 65, 30, 50, new Pair<>(7, 9), 60, 3, 20, new Pair<>(2, 4), 0, new Pair<>(0, 0), 0, 0),
    D84(100, 70, 30, 50, new Pair<>(7, 9), 60, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D85(100, 75, 30, 75, new Pair<>(7, 9), 60, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D86(100, 80, 30, 75, new Pair<>(7, 9), 70, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D87(100, 85, 20, 75, new Pair<>(7, 9), 70, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D88(100, 90, 20, 75, new Pair<>(7, 9), 70, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D89(100, 95, 20, 75, new Pair<>(7, 10), 80, 3, 35, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D90(100, 100, 20, 75, new Pair<>(8, 10), 80, 4, 50, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D91(100, 100, 20, 75, new Pair<>(8, 10), 80, 4, 50, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D92(100, 100, 30, 75, new Pair<>(8, 10), 90, 4, 50, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D93(100, 100, 40, 100, new Pair<>(8, 10), 90, 4, 50, new Pair<>(3, 4), 0, new Pair<>(0, 0), 0, 0),
    D94(100, 100, 50, 100, new Pair<>(8, 10), 90, 4, 50, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D95(100, 100, 60, 100, new Pair<>(8, 10), 100, 4, 50, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D96(100, 100, 70, 70, new Pair<>(8, 10), 100, 4, 70, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D97(100, 100, 80, 80, new Pair<>(10, 10), 100, 4, 70, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D98(100, 100, 90, 90, new Pair<>(10, 10), 85, 5, 70, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D99(100, 100, 100, 100, new Pair<>(10, 10), 85, 5, 100, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D100(100, 100, 100, 100, new Pair<>(10, 10), 85, 5, 100, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0),
    D101(100, 100, 100, 100, new Pair<>(10, 10), 100, 5, 100, new Pair<>(4, 4), 0, new Pair<>(0, 0), 0, 0);

    final int hasArmor; // Percentage chance.
    final int hasFullArmor; // Percentage chance.
    final int hasEnchantedArmor; // Percentage chance.
    final int hasEnchantedArmorFull; // Percentage chance.
    final Pair<Integer, Integer> armorEnchantmentRange; // Enchantment level range.
    final int hasWeapon; // Percentage chance.
    final int weaponType; // Type value.
    final int hasEnchantedWeapon; // Percentage chance.
    final Pair<Integer, Integer> weaponEnchantmentRange; // Enchantment level range.
    final int hasAbility; // Percentage chance.
    final Pair<Integer, Integer> abilityCount; // Count range.
    final int abilityRarity; // Multiplier value.
    final int abilityLevel; // Multiplier value.
}
