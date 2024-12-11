export default class WLSException {
  category: string;
  code: string;
  message: string;
  service: string;

  constructor(
    category: string,
    code: string,
    message: string,
    service: string
  ) {
    this.category = category;
    this.code = code;
    this.message = message;
    this.service = service;
  }
}
