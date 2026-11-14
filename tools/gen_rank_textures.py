#!/usr/bin/env python3
"""Generate serious Minecraft-style 16x16 rank loot / artifact textures."""
from __future__ import annotations
from pathlib import Path
from PIL import Image, ImageDraw

OUT = Path("src/main/resources/assets/myleveling/textures/item")

# Rank palettes: (metal_dark, metal_mid, metal_light, accent, edge, gem)
PALETTES = {
    "e": ((72, 68, 62), (110, 104, 94), (150, 142, 128), (90, 110, 70), (40, 38, 34), (120, 140, 90)),
    "d": ((78, 58, 38), (130, 95, 55), (175, 135, 75), (160, 90, 40), (45, 32, 20), (200, 150, 70)),
    "c": ((45, 58, 72), (75, 100, 125), (120, 150, 175), (55, 130, 160), (25, 32, 40), (90, 180, 200)),
    "b": ((35, 55, 42), (55, 95, 70), (90, 145, 105), (40, 160, 90), (20, 30, 24), (70, 200, 120)),
    "a": ((48, 32, 62), (90, 55, 120), (145, 95, 180), (180, 70, 200), (28, 18, 38), (220, 130, 255)),
    "s": ((28, 26, 30), (70, 65, 55), (180, 155, 70), (220, 180, 50), (12, 10, 14), (255, 230, 120)),
}

def new_img():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))

def put(img, x, y, c, a=255):
    if 0 <= x < 16 and 0 <= y < 16:
        if len(c) == 3:
            c = (*c, a)
        img.putpixel((x, y), c)

def fill_rect(img, x0, y0, x1, y1, c):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            put(img, x, y, c)

def shade_rect(img, x0, y0, x1, y1, dark, mid, light, edge):
    """Beveled metal panel."""
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            if x == x0 or y == y1:
                c = dark
            elif x == x1 or y == y0:
                c = light
            elif x == x0 + 1 or y == y1 - 1:
                c = tuple(max(0, v - 15) for v in mid)
            else:
                # subtle vertical gradient
                t = (y - y0) / max(1, y1 - y0)
                c = tuple(int(mid[i] * (1 - t * 0.25) + dark[i] * (t * 0.25)) for i in range(3))
            put(img, x, y, c)
    # outer edge dark outline where opaque neighbors
    for y in range(y0, y1 + 1):
        for x in (x0 - 1, x1 + 1):
            if 0 <= x < 16:
                put(img, x, y, edge)
    for x in range(x0, x1 + 1):
        for y in (y0 - 1, y1 + 1):
            if 0 <= y < 16:
                put(img, x, y, edge)

def outline_opaque(img, edge=(20, 18, 16)):
    px = img.load()
    copy = img.copy()
    cpx = copy.load()
    for y in range(16):
        for x in range(16):
            if cpx[x, y][3] < 10:
                continue
            for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
                nx, ny = x + dx, y + dy
                if not (0 <= nx < 16 and 0 <= ny < 16) or cpx[nx, ny][3] < 10:
                    # draw edge on neighbor if empty
                    if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3] < 10:
                        put(img, nx, ny, edge)
                    break

def blade(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # tip
    tip = [
        (7, 1), (8, 1),
        (6, 2), (7, 2), (8, 2), (9, 2),
        (6, 3), (7, 3), (8, 3), (9, 3),
    ]
    for x, y in tip:
        put(img, x, y, l if x >= 8 else m)
    # blade body
    for y in range(4, 11):
        put(img, 6, y, e)
        put(img, 7, y, l if y % 2 == 0 else m)
        put(img, 8, y, m if y % 2 == 0 else d)
        put(img, 9, y, e)
        if rank in ("a", "s") and y in (5, 7, 9):
            put(img, 7, y, a)
    # fuller line
    for y in range(4, 10):
        put(img, 7, y, tuple(min(255, v + 20) for v in l) if y % 2 else l)
    # guard
    fill_rect(img, 4, 10, 11, 11, m)
    put(img, 4, 10, l); put(img, 11, 10, d)
    put(img, 5, 10, a); put(img, 10, 10, a)
    # handle
    for y in range(12, 15):
        put(img, 7, y, d)
        put(img, 8, y, m)
    put(img, 7, 15, e); put(img, 8, 15, e)
    # pommel gem
    put(img, 7, 14, g); put(img, 8, 14, a)
    outline_opaque(img, e)
    return img

def axe(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # head
    shade_rect(img, 3, 2, 11, 7, d, m, l, e)
    fill_rect(img, 9, 3, 12, 6, l)
    put(img, 12, 4, a); put(img, 12, 5, a)
    put(img, 4, 3, a); put(img, 5, 3, g)
    # haft
    for y in range(8, 15):
        put(img, 6, y, (90, 60, 35) if rank != "s" else d)
        put(img, 7, y, (120, 80, 45) if rank != "s" else m)
    put(img, 6, 15, e); put(img, 7, 15, e)
    outline_opaque(img, e)
    return img

def hammer(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    shade_rect(img, 2, 2, 13, 7, d, m, l, e)
    # face plates
    fill_rect(img, 2, 3, 4, 6, l)
    fill_rect(img, 11, 3, 13, 6, d)
    put(img, 6, 4, a); put(img, 7, 4, g); put(img, 8, 4, a)
    put(img, 6, 5, g); put(img, 7, 5, a); put(img, 8, 5, g)
    for y in range(8, 15):
        put(img, 7, y, d); put(img, 8, y, m)
    outline_opaque(img, e)
    return img

def bow(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # curved limbs
    pts = [(11,1),(12,2),(12,3),(13,4),(13,5),(13,6),(12,7),(11,8),
           (10,9),(9,10),(8,11),(7,12),(6,13),(5,14),(4,14)]
    for i,(x,y) in enumerate(pts):
        put(img, x, y, l if i % 2 == 0 else m)
        put(img, x-1, y, d)
    # string
    for y in range(2, 14):
        put(img, 4 + (y // 5), y, (200, 200, 190) if rank != "s" else g)
    # grip
    put(img, 10, 7, a); put(img, 11, 7, g); put(img, 10, 8, d)
    outline_opaque(img, e)
    return img

def helm(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    shade_rect(img, 3, 2, 12, 8, d, m, l, e)
    # visor
    fill_rect(img, 4, 5, 11, 7, e)
    put(img, 5, 6, a); put(img, 6, 6, g); put(img, 9, 6, g); put(img, 10, 6, a)
    # crest for high ranks
    if rank in ("a", "s"):
        fill_rect(img, 7, 1, 8, 2, a)
        put(img, 7, 0, g); put(img, 8, 0, g)
    # cheeks
    fill_rect(img, 3, 8, 5, 11, m)
    fill_rect(img, 10, 8, 12, 11, m)
    outline_opaque(img, e)
    return img

def chest(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # torso
    shade_rect(img, 3, 3, 12, 13, d, m, l, e)
    # shoulders
    shade_rect(img, 1, 2, 4, 6, d, m, l, e)
    shade_rect(img, 11, 2, 14, 6, d, m, l, e)
    # center gem / plate
    fill_rect(img, 6, 6, 9, 9, a)
    put(img, 7, 7, g); put(img, 8, 7, g)
    put(img, 7, 8, l); put(img, 8, 8, m)
    # belt
    fill_rect(img, 4, 12, 11, 13, e)
    put(img, 7, 12, g); put(img, 8, 12, a)
    outline_opaque(img, e)
    return img

def legs(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    shade_rect(img, 3, 2, 7, 13, d, m, l, e)
    shade_rect(img, 8, 2, 12, 13, d, m, l, e)
    # knee plates
    fill_rect(img, 4, 7, 6, 9, a)
    fill_rect(img, 9, 7, 11, 9, a)
    put(img, 5, 8, g); put(img, 10, 8, g)
    outline_opaque(img, e)
    return img

def boots(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    shade_rect(img, 2, 5, 6, 12, d, m, l, e)
    shade_rect(img, 9, 5, 13, 12, d, m, l, e)
    # toes
    fill_rect(img, 1, 11, 6, 13, e)
    fill_rect(img, 9, 11, 14, 13, e)
    put(img, 3, 7, a); put(img, 10, 7, a)
    put(img, 4, 7, g); put(img, 11, 7, g)
    outline_opaque(img, e)
    return img

def amulet(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # chain
    for i, (x, y) in enumerate([(5,1),(6,2),(7,3),(8,3),(9,2),(10,1)]):
        put(img, x, y, l if i % 2 == 0 else m)
    # pendant
    shade_rect(img, 5, 5, 10, 12, d, m, l, e)
    fill_rect(img, 6, 6, 9, 10, a)
    put(img, 7, 7, g); put(img, 8, 7, g)
    put(img, 7, 8, l); put(img, 8, 8, m)
    put(img, 7, 9, g); put(img, 8, 9, a)
    outline_opaque(img, e)
    return img

def ring(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    # band
    for x, y in [(5,6),(6,5),(7,4),(8,4),(9,5),(10,6),(10,7),(10,8),(9,9),(8,10),(7,10),(6,9),(5,8),(5,7)]:
        put(img, x, y, m)
    for x, y in [(6,6),(7,5),(8,5),(9,6),(9,7),(9,8),(8,9),(7,9),(6,8),(6,7)]:
        put(img, x, y, (0,0,0,0))  # hole - clear later
    # redraw hole properly
    img2 = new_img()
    band = [(5,6),(6,5),(7,4),(8,4),(9,5),(10,6),(10,7),(10,8),(9,9),(8,10),(7,10),(6,9),(5,8),(5,7),
            (6,6),(9,6),(6,8),(9,8)]
    for x,y in band:
        put(img2, x, y, m)
    put(img2, 7, 4, l); put(img2, 8, 4, l)
    put(img2, 5, 7, d); put(img2, 10, 7, d)
    # gem setting
    put(img2, 7, 2, e); put(img2, 8, 2, e)
    put(img2, 6, 3, e); put(img2, 9, 3, e)
    put(img2, 7, 3, g); put(img2, 8, 3, a)
    outline_opaque(img2, e)
    return img2

def crown(rank: str) -> Image.Image:
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    shade_rect(img, 2, 7, 13, 11, d, m, l, e)
    # points
    for x in (3, 6, 9, 12):
        fill_rect(img, x, 3, x + 1, 7, m)
        put(img, x, 2, g if x in (6, 9) else a)
        put(img, x + 1, 2, a if x in (6, 9) else l)
    put(img, 7, 8, g); put(img, 8, 8, a)
    outline_opaque(img, e)
    return img

def artifact(kind: str) -> Image.Image:
    # kind like artifact_e_luck
    parts = kind.split("_")
    rank = parts[1]
    name = "_".join(parts[2:])
    d, m, l, a, e, g = PALETTES[rank]
    img = new_img()
    if name in ("luck", "xp", "speed"):
        # talisman tablet
        shade_rect(img, 3, 2, 12, 13, d, m, l, e)
        fill_rect(img, 5, 4, 10, 10, a)
        # rune
        for x,y in [(6,5),(7,5),(8,5),(9,5),(7,6),(7,7),(7,8),(6,9),(7,9),(8,9)]:
            put(img, x, y, g)
        put(img, 7, 3, l)
    elif name in ("guard", "fury", "mana", "vamp"):
        # orb
        for y in range(3, 13):
            for x in range(3, 13):
                dx, dy = x - 7.5, y - 7.5
                r2 = dx*dx + dy*dy
                if r2 <= 20:
                    c = l if r2 < 6 else (a if r2 < 14 else d)
                    put(img, x, y, c)
        put(img, 6, 6, g); put(img, 7, 6, (255,255,255))
        put(img, 7, 7, g); put(img, 8, 8, m)
        # mount
        fill_rect(img, 6, 12, 9, 14, e)
    else:  # system / default
        shade_rect(img, 2, 2, 13, 13, d, m, l, e)
        fill_rect(img, 4, 4, 11, 11, e)
        for i in range(4):
            put(img, 5 + i, 5, g)
            put(img, 5 + i, 10, a)
            put(img, 5, 5 + i, a)
            put(img, 10, 5 + i, g)
        put(img, 7, 7, g); put(img, 8, 7, a)
        put(img, 7, 8, a); put(img, 8, 8, g)
    outline_opaque(img, e)
    return img

def boss_drop(name: str) -> Image.Image:
    # thematic colors per drop
    themes = {
        "grove_fang": ((40,55,30),(70,100,50),(120,160,80),(180,200,100)),
        "scorpion_stinger": ((60,40,20),(120,80,30),(180,130,50),(220,180,60)),
        "frost_core": ((30,50,80),(60,110,160),(140,200,230),(220,245,255)),
        "witch_grimoire": ((40,30,50),(80,50,90),(120,80,140),(180,140,200)),
        "reef_pearl": ((20,60,70),(40,120,130),(100,200,190),(200,255,240)),
        "ash_ember": ((60,20,10),(140,50,20),(220,100,30),(255,180,60)),
        "thunder_core": ((40,40,20),(100,100,40),(200,190,60),(255,255,140)),
        "oracle_shard": ((50,40,80),(100,70,150),(160,120,220),(230,200,255)),
        "crimson_sigil": ((50,10,10),(120,25,25),(180,40,40),(255,80,80)),
        "abyss_plate": ((20,30,50),(40,60,90),(70,100,140),(120,160,200)),
        "sandstorm_veil": ((70,55,30),(140,110,55),(200,170,90),(240,220,150)),
        "void_scythe": ((20,10,30),(50,25,70),(90,40,120),(160,80,200)),
        "behemoth_heart": ((40,10,10),(100,20,20),(160,30,40),(220,60,70)),
        "glacial_crown": ((40,60,90),(80,120,160),(160,200,230),(230,245,255)),
        "gate_key": ((30,30,40),(80,70,50),(160,140,70),(220,200,100)),
        "kv2_howitzer": ((40,45,30),(70,80,45),(110,120,70),(160,150,80)),
    }
    d, m, l, a = themes.get(name, ((50,50,50),(90,90,90),(140,140,140),(200,200,200)))
    e = tuple(max(0, v - 25) for v in d)
    img = new_img()
    if "fang" in name or "stinger" in name or "scythe" in name:
        for y in range(1, 14):
            x = 4 + y // 3
            put(img, x, y, l if y < 5 else m)
            put(img, x + 1, y, d)
            put(img, x - 1, y, e)
        put(img, 5, 1, a); put(img, 6, 2, a)
    elif "core" in name or "pearl" in name or "ember" in name or "heart" in name:
        for y in range(3, 13):
            for x in range(3, 13):
                dx, dy = x - 7.5, y - 7.5
                if dx*dx + dy*dy <= 18:
                    put(img, x, y, a if dx*dx+dy*dy < 5 else (l if dx*dx+dy*dy < 11 else d))
        put(img, 6, 6, (255,255,255)); put(img, 7, 6, a)
    elif "grimoire" in name:
        shade_rect(img, 3, 2, 12, 13, d, m, l, e)
        fill_rect(img, 5, 4, 10, 11, e)
        for y in (5, 7, 9):
            fill_rect(img, 6, y, 9, y, a)
    elif "crown" in name or "sigil" in name or "veil" in name or "plate" in name:
        shade_rect(img, 2, 4, 13, 11, d, m, l, e)
        fill_rect(img, 5, 6, 10, 9, a)
        put(img, 7, 7, (255,255,255)); put(img, 8, 7, l)
        if "crown" in name:
            for x in (3, 7, 11):
                put(img, x, 3, a); put(img, x, 2, l)
    elif "shard" in name:
        pts = [(8,1),(9,2),(10,3),(11,5),(10,7),(9,9),(8,11),(7,12),(6,11),(5,9),(4,7),(5,5),(6,3),(7,2)]
        for x,y in pts:
            put(img, x, y, l)
        fill_rect(img, 6, 5, 9, 8, a)
        put(img, 7, 6, (255,255,255))
    elif "key" in name:
        shade_rect(img, 5, 1, 10, 6, d, m, l, e)
        put(img, 6, 2, a); put(img, 9, 2, a)
        for y in range(7, 14):
            put(img, 7, y, m); put(img, 8, y, l)
        put(img, 9, 11, m); put(img, 10, 11, l)
        put(img, 9, 13, m); put(img, 10, 13, d)
    elif "howitzer" in name:
        # small tank gun item icon 32x32 actually - keep 16 for now then scale
        shade_rect(img, 1, 6, 14, 10, d, m, l, e)
        fill_rect(img, 12, 5, 14, 11, e)
        fill_rect(img, 4, 4, 8, 6, m)
        put(img, 5, 5, a)
    else:
        shade_rect(img, 3, 3, 12, 12, d, m, l, e)
        put(img, 7, 7, a); put(img, 8, 7, l)
    outline_opaque(img, e)
    return img

def save(img: Image.Image, name: str, size=16):
    if size != 16:
        img = img.resize((size, size), Image.NEAREST)
    path = OUT / f"{name}.png"
    img.save(path)
    print("wrote", path)

def main():
    OUT.mkdir(parents=True, exist_ok=True)
    gens = {
        "blade": blade, "axe": axe, "hammer": hammer, "bow": bow,
        "helm": helm, "chest": chest, "legs": legs, "boots": boots,
        "amulet": amulet, "ring": ring, "crown": crown,
    }
    # existing files dictate what to regenerate
    for p in sorted(OUT.glob("rank_*.png")):
        stem = p.stem  # rank_e_blade
        parts = stem.split("_", 2)
        if len(parts) < 3:
            continue
        _, rank, kind = parts
        if kind in gens and rank in PALETTES:
            save(gens[kind](rank), stem)

    for p in sorted(OUT.glob("artifact_*.png")):
        save(artifact(p.stem), p.stem)

    drops = [
        "grove_fang","scorpion_stinger","frost_core","witch_grimoire","reef_pearl",
        "ash_ember","thunder_core","oracle_shard","crimson_sigil","abyss_plate",
        "sandstorm_veil","void_scythe","behemoth_heart","glacial_crown","gate_key",
    ]
    for name in drops:
        save(boss_drop(name), name)

    # howitzer item is 32x32 in existing - regenerate richer
    hw = boss_drop("kv2_howitzer").resize((32, 32), Image.NEAREST)
    # add more detail on 32 canvas
    draw = ImageDraw.Draw(hw)
    # reinforce silhouette
    save(hw, "kv2_howitzer", 32)

    # recount colors
    for sample in ["rank_e_blade","rank_s_blade","rank_a_chest","artifact_s_system","frost_core"]:
        im = Image.open(OUT / f"{sample}.png")
        print(sample, "colors", len(im.getcolors(65536) or []))

if __name__ == "__main__":
    main()
