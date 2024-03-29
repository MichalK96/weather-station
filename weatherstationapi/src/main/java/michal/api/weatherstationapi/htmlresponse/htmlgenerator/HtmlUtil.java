package michal.api.weatherstationapi.htmlresponse.htmlgenerator;

class HtmlUtil {

    static String generateHtml(String name, String body) {
        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                        <style>
                            table, th, td {
                                font-size: 20px;
                                border: 2px solid black;
                                border-collapse: collapse;
                                padding: 8px;
                            }
                            p {
                                font-size: 20px;
                               }
                        </style>
                </head>
                %s
                </html>
                                
                """, name, body);
    }

}
