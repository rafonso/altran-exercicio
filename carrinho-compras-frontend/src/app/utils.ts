export class Utils {

  static handleError(err) {
    console.error(err);
    return err.message;
  }
}
