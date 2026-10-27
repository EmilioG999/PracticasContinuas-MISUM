#!/usr/bin/env python3
"""Genera docs/site/ a partir de las carpetas boletinX/ en docs/."""
import pathlib
import re
import shutil
import markdown

DOCS_DIR = pathlib.Path(__file__).parent
SITE_DIR = DOCS_DIR / "site"

TEMPLATE = """<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="utf-8">
<title>{title}</title>
<script src="https://cdn.jsdelivr.net/npm/mermaid@10.9.1/dist/mermaid.min.js"></script>
<style>
  body {{ font-family: system-ui, sans-serif; max-width: 860px; margin: 2rem auto; padding: 0 1rem; line-height: 1.6; color: #1a1a1a; }}
  img {{ max-width: 100%; }}
  pre {{ background: #f4f4f4; padding: 1rem; overflow-x: auto; border-radius: 6px; }}
  code {{ background: #f4f4f4; padding: 0.15rem 0.35rem; border-radius: 4px; }}
  pre code {{ background: none; padding: 0; }}
  table {{ border-collapse: collapse; width: 100%; }}
  th, td {{ border: 1px solid #ddd; padding: 0.5rem; text-align: left; }}
  a {{ color: #0b5fff; }}
  nav a {{ margin-right: 1rem; }}
</style>
</head>
<body>
<nav><a href="{nav_back}">&larr; Indice</a></nav>
{body}
<script>
  document.querySelectorAll('pre code.language-mermaid').forEach(function (block) {{
    var div = document.createElement('div');
    div.className = 'mermaid';
    div.textContent = block.textContent;
    block.parentNode.replaceWith(div);
  }});
  mermaid.initialize({{ startOnLoad: true }});
</script>
</body>
</html>
"""

def titulo_de(md_text: str, fallback: str) -> str:
    m = re.search(r"^#\s+(.+)$", md_text, re.MULTILINE)
    return m.group(1).strip() if m else fallback

def main():
    if SITE_DIR.exists():
        shutil.rmtree(SITE_DIR)
    SITE_DIR.mkdir(parents=True)

    boletin_dirs = sorted(
        d for d in DOCS_DIR.iterdir()
        if d.is_dir() and d.name != "site" and (d / f"{d.name}.md").exists()
    )

    entradas = []
    for carpeta in boletin_dirs:
        nombre = carpeta.name
        md_path = carpeta / f"{nombre}.md"
        texto = md_path.read_text(encoding="utf-8")
        titulo = titulo_de(texto, nombre)
        html_body = markdown.markdown(
            texto, extensions=["extra", "tables", "fenced_code", "sane_lists"]
        )

        destino = SITE_DIR / nombre
        destino.mkdir(parents=True, exist_ok=True)

        capturas_src = carpeta / "capturas"
        if capturas_src.is_dir():
            shutil.copytree(capturas_src, destino / "capturas", dirs_exist_ok=True)

        html_final = TEMPLATE.format(title=titulo, body=html_body, nav_back="../index.html")
        (destino / "index.html").write_text(html_final, encoding="utf-8")
        entradas.append((nombre, titulo))

    items = "\n".join(
        f'<li><a href="{nombre}/index.html">{titulo}</a></li>' for nombre, titulo in entradas
    )
    index_body = f"<h1>Practicas Continuas - Documentacion</h1>\n<ul>\n{items}\n</ul>"
    (SITE_DIR / "index.html").write_text(
        TEMPLATE.format(title="Indice", body=index_body, nav_back="index.html"), encoding="utf-8"
    )
    print(f"Generadas {len(entradas)} paginas en {SITE_DIR}")

if __name__ == "__main__":
    main()
