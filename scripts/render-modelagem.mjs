import fs from "node:fs";
import path from "node:path";

const root = process.cwd();
const jobs = [
  ["docs/modelagem/01-nucleo-institucional.mmd", "docs/modelagem/01-nucleo-institucional.svg", "Núcleo institucional", "#155e9a"],
  ["docs/modelagem/02-conteudo-comunicacao.mmd", "docs/modelagem/02-conteudo-comunicacao.svg", "Conteúdo e comunicação", "#287a4d"],
  ["docs/modelagem/03-administracao-seguranca.mmd", "docs/modelagem/03-administracao-seguranca.svg", "Administração e segurança", "#71308d"]
];

const escapeXml = value => value
  .replaceAll("&", "&amp;")
  .replaceAll("<", "&lt;")
  .replaceAll(">", "&gt;")
  .replaceAll('"', "&quot;");

function parse(source) {
  const lines = source.split(/\r?\n/);
  const entities = [];
  const relationships = [];
  let current = null;

  for (const line of lines) {
    const entityStart = line.match(/^\s*([A-Z_]+)\s*\{\s*$/);
    if (entityStart) {
      current = { name: entityStart[1], fields: [] };
      entities.push(current);
      continue;
    }
    if (current && /^\s*}\s*$/.test(line)) {
      current = null;
      continue;
    }
    if (current) {
      const field = line.trim();
      if (field) current.fields.push(field);
      continue;
    }
    const relation = line.match(/^\s*([A-Z_]+)\s+([^\s]+)--([^\s]+)\s+([A-Z_]+)\s*:\s*(.+)$/);
    if (relation) {
      relationships.push({
        left: relation[1],
        leftCardinality: relation[2],
        rightCardinality: relation[3],
        right: relation[4],
        label: relation[5].replaceAll("_", " ")
      });
    }
  }
  return { entities, relationships };
}

function render(input, output, title, accent) {
  const { entities, relationships } = parse(fs.readFileSync(input, "utf8"));
  const width = 1400;
  const columns = entities.length > 10 ? 4 : 3;
  const gap = 22;
  const margin = 32;
  const cardWidth = (width - margin * 2 - gap * (columns - 1)) / columns;
  const headerHeight = 38;
  const fieldHeight = 21;
  const cards = [];
  let y = 92;

  for (let index = 0; index < entities.length; index += columns) {
    const row = entities.slice(index, index + columns);
    const rowHeight = Math.max(...row.map(entity => headerHeight + 18 + entity.fields.length * fieldHeight));
    row.forEach((entity, column) => {
      cards.push({ entity, x: margin + column * (cardWidth + gap), y, height: rowHeight });
    });
    y += rowHeight + gap;
  }

  const relationTop = y + 16;
  const relationColumns = 2;
  const relationWidth = (width - margin * 2 - gap) / relationColumns;
  const relationsPerColumn = Math.ceil(relationships.length / relationColumns);
  const relationHeight = 26 + relationsPerColumn * 23 + 24;
  const height = relationTop + relationHeight + 32;

  const outputParts = [
    `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}" viewBox="0 0 ${width} ${height}">`,
    `<rect width="100%" height="100%" fill="#f5f7fa"/>`,
    `<rect x="0" y="0" width="${width}" height="66" fill="${accent}"/>`,
    `<text x="${margin}" y="42" font-family="Arial, sans-serif" font-size="26" font-weight="700" fill="#ffffff">${escapeXml(title)}</text>`,
    `<text x="${width - margin}" y="40" text-anchor="end" font-family="Arial, sans-serif" font-size="14" fill="#ffffff">Modelo lógico proposto para discussão</text>`
  ];

  for (const { entity, x, y: cardY, height: cardHeight } of cards) {
    outputParts.push(`<rect x="${x}" y="${cardY}" width="${cardWidth}" height="${cardHeight}" rx="8" fill="#ffffff" stroke="#cbd5e1"/>`);
    outputParts.push(`<path d="M ${x + 8} ${cardY} H ${x + cardWidth - 8} Q ${x + cardWidth} ${cardY} ${x + cardWidth} ${cardY + 8} V ${cardY + headerHeight} H ${x} V ${cardY + 8} Q ${x} ${cardY} ${x + 8} ${cardY}" fill="${accent}"/>`);
    outputParts.push(`<text x="${x + 14}" y="${cardY + 25}" font-family="Arial, sans-serif" font-size="15" font-weight="700" fill="#ffffff">${escapeXml(entity.name)}</text>`);

    entity.fields.forEach((field, fieldIndex) => {
      const fieldY = cardY + headerHeight + 22 + fieldIndex * fieldHeight;
      const parts = field.split(/\s+/);
      const constraints = parts.slice(2).join(" ");
      outputParts.push(`<text x="${x + 14}" y="${fieldY}" font-family="monospace" font-size="12" fill="#334155">${escapeXml(parts[0])}</text>`);
      outputParts.push(`<text x="${x + 100}" y="${fieldY}" font-family="Arial, sans-serif" font-size="13" fill="#111827">${escapeXml(parts[1] ?? "")}</text>`);
      if (constraints) {
        outputParts.push(`<text x="${x + cardWidth - 12}" y="${fieldY}" text-anchor="end" font-family="Arial, sans-serif" font-size="11" font-weight="700" fill="${accent}">${escapeXml(constraints)}</text>`);
      }
    });
  }

  outputParts.push(`<rect x="${margin}" y="${relationTop}" width="${width - margin * 2}" height="${relationHeight}" rx="8" fill="#ffffff" stroke="#cbd5e1"/>`);
  outputParts.push(`<text x="${margin + 14}" y="${relationTop + 23}" font-family="Arial, sans-serif" font-size="15" font-weight="700" fill="${accent}">RELACIONAMENTOS</text>`);

  relationships.forEach((relationship, index) => {
    const column = Math.floor(index / relationsPerColumn);
    const row = index % relationsPerColumn;
    const x = margin + 14 + column * (relationWidth + gap);
    const relY = relationTop + 50 + row * 23;
    const text = `${relationship.left} ${relationship.leftCardinality} — ${relationship.rightCardinality} ${relationship.right} · ${relationship.label}`;
    outputParts.push(`<text x="${x}" y="${relY}" font-family="Arial, sans-serif" font-size="12" fill="#334155">${escapeXml(text)}</text>`);
  });

  outputParts.push(`</svg>`);
  fs.writeFileSync(output, outputParts.join("\n"));
}

for (const [inputRelative, outputRelative, title, accent] of jobs) {
  render(path.join(root, inputRelative), path.join(root, outputRelative), title, accent);
}
