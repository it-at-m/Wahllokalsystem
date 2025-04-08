import type { Ref } from "vue";

export function htmlFromData(
  data: any,
  desserts: Ref<{ name: string; calories: number }[]>
) {
  return `
    <!DOCTYPE html>
      <html lang="de">
      <head>
        <meta charset="utf-8"/>
        <style>
          /****** Print Header ******/
          @page {
              size: auto;
              margin-top: 0.0cm;
              margin-left: 0;
              margin-right: 0;
          }

          @media print {
               div.footer {
                   position: fixed;
                   bottom: 0;
                   left: 1cm;
                   font-size: x-small;
                   z-index: 0;
               }
           }

          /****** Default Value Tags ******/
          body {
              max-width: 21cm;
              margin: 0;
              font-size: 12pt;
              writing-mode: lr-tb;
              text-align: left;
              font-family: Arial, serif;
          }

          html, body {
              overflow-x: hidden;
          }

          /****** Table ******/
          table {
              border-collapse: collapse;
          }

          .table > tr > th,
          .table > tr > td {
              padding-top: 2px;
              padding-bottom: 2px;
          }

          th,
          td {
              font-weight: normal;
              padding: 0.12cm 0.2cm;
              border: 1px solid #000000;
          }

          /** styles **/
          .horizontal {
              display: flex;
          }

          .vertical {
              display: flex;
              flex-direction: column;
          }

          .spaceBetween {
              justify-content: space-between;
          }

          .widthLeft {
              width: 10cm;
              margin-right: 0.4cm;
          }

          .widthRightTop {
              width: 6.7cm;
          }

          .margin-top {
              margin-top: 1cm;
          }

          .margin-leftright {
              margin-left: 1cm;
              margin-right: 1cm;
          }

          .smallfont {
              font-size: 9pt;
          }
       </style>
        <title>Testdruck</title>
      </head>
      <body>
        <h1 class="margin-top margin-leftright">${data.title || "Alternativer Titel"}</h1>
        <div class="horizontal spaceBetween margin-leftright">
          <div class="vertical widthLeft">
            <p>Für deinen perfekten Nachtisch hast du folgende Einstellungen getroffen: mehr text um zu sehen was passiert wenn der platz nicht reicht</p>
            <br />
            <p>Kuchen: ${data.cake || "Standardkuchen"}</p>
            <p>Stücke: ${data.cakeNumber || "2 schaffst du bestimmt!"}</p>
            <p>Toppings: ${data.toppings || "Weniger ist mehr."}</p>
            <p>Hunger Index: ${data.hungerIndex[0] || "Hier ist was schief gelaufen"} - ${data.hungerIndex[1] || "Das Messgerät spinnt."}</p>
            ${
              data.hungerIndex[1] <= 20
                ? `<p>
                     Hinweis:
                     <i>Du scheinst satt zu sein. Ist noch was vom Kuchen übrig?</i>
                   </p>`
                : data.hungerIndex[0] >= 50
                  ? `<p>
                     Hinweis:
                     <i>Du bist ziemlich hungrig, du solltest dir jetzt einen Kuchen backen! Und am besten gleich noch was für deine Kollegen mitbringen</i>
                   </p>`
                  : ``
            }
            <br />
            <br />
          </div>
          <div class="vertical spaceBetween widthRightTop marginTopBottom_1">
            <h4>Kalorienliste</h4>
            <table>
              <thead>
                <tr>
                  <th class="text-left">Name</th>
                  <th class="text-left">Kalorien</th>
                </tr>
              </thead>
              <tbody>
                ${desserts.value
                  .map(
                    (dessert) => `
                    <tr>
                      <td>${dessert.name}</td>
                      <td>${dessert.calories}</td>
                    </tr>`
                  )
                  .join(``)}
              </tbody>
            </table>
          </div>
        </div>
        <div class="margin-top margin-leftright">
          <h3>Aus offensichtlichen obligatorischen Gründen:</h3>
          <span class="smallfont">und um den seitenumbruch zu testen</span>
          <p>
              Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
              et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
              Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
              consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
              sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
              takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed
              diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et
              accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum
              dolor sit amet. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel
              illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto odio dignissim qui blandit praesent
              luptatum zzril delenit augue duis dolore te feugait nulla facilisi. Lorem ipsum dolor sit amet, consectetuer
              adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat.
              Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea
              commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse.
              Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore
              et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.
              Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet,
              consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,
              sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea
              takimata sanctus est Lorem ipsum dolor sit amet. 
          </p>
        </div>
      </body>
      </html>`;
}
