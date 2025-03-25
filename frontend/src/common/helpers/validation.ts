export const isZipCode = (text: string): boolean => {
  const regex = /^\d{2,5}(?:-\d{1,4})?$/;
  return regex.test(text);
};

export const optionalZipCode = (text: string): boolean => {
  return !text ? true : isZipCode(text);
};

export const isPhoneNumber = (text: string): boolean => {
  const regex = /^\+?[0-9\s-]{7,20}$/;
  return regex.test(text);
};

export const optionalPhoneNumber = (text: string): boolean => {
  return !text ? true : isPhoneNumber(text);
};
