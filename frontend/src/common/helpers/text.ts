export const stripHtmlTags = (input: string): string => {
  if (input === null) return '';
  return input.replace(/<[^>]*>/g, '');
};

export const htmlEncode = (str: string): string => {
  return str.replace(/[&<>"']/g, function(match) {
    return `&#${match.charCodeAt(0)};`;
  });
};

export const removeExtraSpaces = (str: string): string => {
  return str.replace(/\s+/g, ' ').trim();
};


export const toTitleCase = (str: string): string => {
  return str
    .toLowerCase()
    .replace(/\b\w/g, char => char.toUpperCase());
};

export const toCamelCase = (str: string): string => {
  return str
    .replace(/(?:^\w|[A-Z]|\b\w|\s+)/g, (match, index) => index === 0 ? match.toLowerCase() : match.toUpperCase())
    .replace(/\s+/g, '');
};

export const toKebabCase = (str: string): string => {
  return str
    .toLowerCase()
    .replace(/\s+/g, '-')
    .replace(/[^\w\s-]/g, '');
};

export const toSnakeCase = (str: string): string => {
  return str
    .toLowerCase()
    .replace(/\s+/g, '_')
    .replace(/[^\w\s]/g, '');
};

export const truncate = (str: string, maxLength: number): string => {
  if (str.length <= maxLength) return str;
  return str.slice(0, maxLength) + '...';
};

export const extractUrls = (text: string): string[] => {
  const urlRegex = /(https?:\/\/[^\s]+)/g;
  return text.match(urlRegex) || [];
};

export const wordCount = (str: string): number => {
  return str.trim().split(/\s+/).length;
};

export const slugify = (text: string): string => {
  return text
    .toString()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '')
    .replace(/--+/g, '-')
    .replace(/^-+/, '')
    .replace(/-+$/, '');
};
