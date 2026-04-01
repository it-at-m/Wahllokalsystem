import type { NiederschriftDruckInput } from "@/types/ergebnismeldung/common/NiederschriftDruckInput.ts";

export function useNiederschriftDrcuk() {
  function _dataForHeader(data: NiederschriftDruckInput) {
    return `
     <head>
                <meta charset="utf-8"/>
                ${_getStyling()}
                <title>${data.aktuelleWahl.wahlart} Briefwahl Niederschrift</title>
            </head>
            <body>
            <svg height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
            <!-- Title -->
            <div class="width_100 textAlignCenter marginTop_1_15"><span class="bold fontSize_11">V1a MigBW</span></div>
            
            <!-- Header -->
            <div class="gridContainer_3_column_header marginTop_2 marginBottom_5">
                <!-- Barcode -->
                <div>
                    <img class="barcode"
                        src="${data.barcode}"
                        alt="">
                </div>
                <div class="horizontal flexEnd">
                    <img style="height: 1.50cm; margin-top: 0.1cm;"
                        alt=""
                        src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGAAAAB4CAYAAAANHffOAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAAABmJLR0QA/wD/AP+gvaeTAAAAB3RJTUUH4QgJDi03zr20JgAAEaxJREFUeNrtnXlYVeW+xz9r7Y3IJALK4MTgiYBIc0AhvWkeM0vN0/FoVo/atczqqaxreXuOXvVcp5N5y6OerhVZPQ7nVs5ZmpY44ISEYgICMigqMSgybQT2Xu/9Y++NoEgMe1LW93neh7UXa73rfX/f9ze801oS8D7QHxX2QLYW6AeMVGVhF5zRmo+ee+45hg0bporEBjh16hRr164FoI6AoUOH8vLLL6vSsQG2bt1aR4CsisO+UAlQCVAJUKESoBKgQiVAJUCFSoBKgAqVgPYF7d1QSKXgHOLaNiSnfeCaypY9rqz6rJLKqo64ufvg7uFLly5dCQoKIiQkhPDwcAYOHIhWq1UJaCsMafsxZB03/RqJ5PwQf3lkC48NKeTf34Ltey42ep+HhwfDhg3jtdde44knnlBNUGsgdKUYsk+w8rtEvvj5DHtP5bD3RDnpByfQSYng209h5L81fm95eTm7du3iySefZMiQIaSmpqoEtJiAkksgBGcvFJFfUkF8Wh47TmSy9NuT6HMmoTF04R+Lfj+fo0ePEhUVxYYNG1QT1CICaioBWDXjMVydtdQaFKprDWgkQHFCKRpAROiPdPZ05nppdZN56XQ6pk2bhlarZfLkyaoGNM8B6AFwddayJymbDQfOIksSLs5ORoJqPQHw83VrnjNXFKZNm0ZiYqKqAc2TmAGAolIds7/YjxCCzCslLJ1imjo1dABAo5GanWVNTQ1Tp04lKSmJjh07qhrQJGqNZqWrpys7/jqBlS+OZP7kITf/LxkFr9W2rBppaWmsXLnScqZSr79HnXCNru74DwFejOoXTEenekorhLESstTivD/66COqqqraqKEKxd9+yy/h4ZQePmwbApL2biFp3QJEdWXducunDoC+xgphaEnTF0hGArSalrejwsJC1q9f3ybBJ4aHkzZpElXnz5O3eLFtCPjwky/YdySBop3/Q23BeQByj36P7kAsovKaZR1U5ONIbl5NEKAYLZVeaVX+3333XYvvubpjB79ERhoFn5FRd75k717Kjx+3LgG68lLmjn0QgKnvbyT/5/XUpsUx76u9VFy/Su3hL1AKMi1GgOTpj+TSuYkLjE76xo3W2eDDhw9jMBhadE9FUhK6tDQkrQbf0VH4jo6q+9/FVmhBi6IgVw9PwqctxiNkC1N/S2PJpr2s8HDm71OGM37+OuI+fBN94mY0EX9EEzzIMiy4dmqCAH2TBPj4+DBy5EjCwsJwcXEBoHNnI6HXr18HoKysDC8vr2YXp/ubb1CTHEfAU9E4eXsQty2e60B34Nr331N+8iQeUVFWDENlDT2GTUJUFPNfHj5IhlL6BfsxY/RAfvj1MmPDvTGk/ITk0hnZP7TN8tf06o/sGYDQV4OkQdI6oRT9iJKvgGwioPr2VjxhwgTWr19fJ3iLRMWFWYjUnwh8aTSFWVd4cdF6UnJ/Y2m9a/KWLiVi2zbrR0GSexc+3H6MQbP+yYaDZ+kf2pMvtu6uV1q9ZcxQ5wDkwP5oesegCRnEVW0J2m6LkdzzkEwmqLKy9rb7hgwZYlHhi8oS9AlfIyqucjK7mNA5n9Ax9zfWAcG3+IjK5GTrE3Bo/0/IpVf4cvZEgv286N3Nl0NnsqgxGB2iqLCsQwbYv38/YZFPkZJeibbH9yDXcqMaKnW3R2CrV6+mqKjIcv7IxaOu3/HJzoNMDg3jHeA2ioXg4pIl1ifA70o8Qx/szbyv9tI3yBdnWeFPwwfx9trvEEgYso6h5CVzo0rX5spXVFQwd+5cRo0axbWSSl6cDWu+zuOpOXH4RNR1BxogJyeHoUOHkplpmaBA3CgHySiuID8v9l29ikGjaYQpCUmSEM107hpgCtB7zJgxRLXAefj0up+QThLPRAXSQWssyOiBoew+kcrImH5olWqUgkx8Y8aTkpKKoih4e3vj7u7e7GecP3+e1atXM3XqVPbs2YMwSfpyPuzeD5m5CrVNWLqrV68SGxsLwKBBg1o8QXPp0iW2fL2JLgW/4JITD0JBEYLFm/Zj0FfTJTSCnpcv113fKSaGsE2b6D57NpIsN9kT/+abbwAKJGAv8NjHH3/Mq6++2nLHVJSD4dx+RGnB7a1GCDo+Na/Bufvuu4+oqCgiIiIIDQ0lMDAQjUZDbW0tly5d4sKFCyQnJxMXF8elS5csZkK8vb15+umniYmJoVevXvTo0QM/Pz90Oh01NTVUVlaSlZVFeno66enpHD9+nLS0NABOrnqdPsEBHDqby6KNPxHk58WUkf15/dOf+SQ3F/ewMAL/9je6TJzYrLJs3bqVCRMmQP39Aa0ey+gajNxlOsqVVAzpBxG66zcJaOT6zMzM3zULU/4CC96CGe9YaMBLhtgPrrF7/+e89NLnrc7ncnEp0x+PYtIjD3K9spq//nUOkT2D8R45Eqkxc2Sz0VBJQu7+AHK3cPQXTqE/dxBZf6PV2f1pNPz5SdgTB1u+b3vx3nwRnn4CfitsWz7PDu+LEIIzxXrCnnqdqT5+be/tWzRMkWS0QQPQ9uyDITcRQ3p8q7J5bwk89ghsWAPOHWDTttYX6e2XYcV8yMqF+R+0tWfuh1PkaKK8ultMZNYZDdU4oekdg9Pwma26PTMHxkyBG9Ww8Z+wbR2EhrQsj/uCYfNn8OFCyLsCI5+B4tZGxlpntA+OxmnodCQLCt96BJjRwbXVtx4+AX3/CPsOGU1S2iH48V8wbRIE92r8ngA/mDrRKPi0QzBhDBw5CYPHQG5e66vhFDURObB/XT/AogOO1pS/aCxAbwEuXoZRk43+4L3XYdQwYwIoKTXa9LJy8OwEAb7Gv2Zk5cJ/fwQbtoCitLEiTtabObsrFmZt/cGYHnoAHh8OMQMhsAf8IQjc3aC0DDKy4VI+JKfCrn2Q9GvjHTRHg0NrwK04nWJMZqxaDG9Mh1Wft93B2gvq2tB7mQBxN9gAVQNUAhxOA5ydna1SnunTpxMdHa1qwJ3g5+fHunXrSElJsUr+Dz/8MEeOHGHRokVIVojp7+ooaPDgwWzbto2AgAAKCwut1+JkmXnz5qEoCgsWLFA1AOD+++9n9+7dBAQE2OyZc+fOJSYmRvUBALGxsS1amWAJaDQa1q5da3dTZHcCoqOjGTp0qF0q36dPH0aNGtW+TdDgwYPt+vxnnnmmfWtAS+aIrYGwsDDVCdsTvr6+qhO2J+ytgXYnwN5RiCVXzzlcR6wt8HCH0Y82fc19pjWBEaEwcVzT1ybfoXNt721KWkfVgABf+OaT5j1nwhhjagr/sbDx805OTqoGNIbScvj0d7b1Rg+APuFwIsk4E9YUfk2DBwY13gAkSbKbv3LYGbGCIpg5p+lrVsw3ErDlB/jg49/Pc/K0OzhCWW7xRo17Jgx1hFFJWbafGNQZMYzjQmoYqmpA+0W71gBHgKoBqga0bw1Q2rx2UQ1DVQLuZg2wZznVMPReJuBuQbvWANUJQw0YX+XVXp2wrRtKPVnXyEAFGHejq07YNigvL687lIHyW06qGmBllJWVNSCgwloEqD6geRpQCjdfYKSaIOujnqzLZOACGF+MYZcoQJbbHQFZWVnmw1wZSAdIT0+3S8XsvSpBCGFzE1RP1ukycA6gtLTUquvyHZUAWwu/traW3Nxc888MGbgGFAOcO3fO5hrg5uZmVwJkWbZpJJadnU1tbS2AAcgyG+CzACdPnrS5AHr27GlXAiRJsunaoISEhLrWb+6IARwEOHDggM01IDQ0FHvDz8/PZs+Ki4szHx4wD0XU/WjNi0zbgoEDB9KjRw+7ExAZGWkPAuLqE3AMqCotLSUpKckmGqDValm1apVD9ANGjBhhk+dcvHjR7IAFcKg+AdUmEti9e7dNCrNixQqH2CQHMGPGDJuYoXqyTQEK6hMAsB2w6HdWGtOADh06sHr1ambNmuUwPWFPT0927dpF9+7drfqcjRs3mg8bfQdYV4xD0yIhIUFYAjk5OcKkbiIgIEC88cYbIi0t7XfvKy4uFiEhIXXJx8dHeHl5tTqZyxAbG9vkc0tLS8WyZcuEv79/3T2AOH36dJtlkZubKyRJMud5x31RuwAxa9YsixBQVVUlkpOTRX5+vjAYDOJugV6vF0ePHhULFy4UEydOFOfPn29znkuWLDELv0Gsf2sP5Flgk6+vLxcuXHCIb6wU79hB4Vdf1XWWXMLDCbrDa+JrLl8mc+ZMZNO7JmQ3N+7/8kvjeyvtPNwRERFh7ui+Day807WuQBEg1q5da/+maDCIpAEDxCGoSynjxt3xcl1GhjjaqVPdtYe1WpG3fLndq7F9+3Zz668CGuwKvLVp6IA1AMuXL0ev19u15RRu2oTu7Nnm92q12gatXej1FHz+ObVXr9q1HsuXLzcfrgMKmyIAYDVQnp2dzZYtW+xW6KqMDPKWLUOprm7+uE6HDnDL+6F16elkTJtmtxfIHThwgKNHj5rHfj68rcyN3HMN+Axg6dKldlkxUJWRQer48eha+P1HJ39/tJ6et52/vm8fmTNn2oWAxTf91f8BWbf+/06rUlOAVwoKCpwCAgIYOHCgzQp848IF0saPR2d6cXa9UTPcH3qIbm+9hWt4eOMmSJapzs3lRnY2SuXNrzwJg4HKlBSqL17EZ9w4m9Vl+/btLFu2zNz6nzf512ZjPiC8vb1FUVGRTZyVUlMjTsfENHC6h0Ac79ZNnH/tNWHQ6ZqVT/mpUyJ5xAhx2Nm5QT5HXFxE9pw5NqmLTqcTwcHBZue7ujUEOmOcLRMzZ860SaFz588Xh2T5ZhSj0YjTDz8sKn/9tVX55b3/vkgIDGxAwolu3UR1Xp7V6zJ//nyz8IsBn9Zq0VhAyLIs4uPjrdz8FfFLnz43W72/v8h+5x2h6PVtyrb81CnxS79+DUjImTvXqlVJTU0VHTt2NBPwYltN2TZA9OrVS1y7ds1qhb4eHy/iXVzEIUkSp6KiRNmJE5br2ZaXi5Tx40W8q6s4BOLc889b1fT06dPHLPwjWGD5pzfGlRNi7NixQlEUqxQ8b8UKcdTTU2TMmCGU6mqrPOO32FiRGBkpkh991GoEvPLKK2bhl9DwA0ttwiOAHhBr1qyxSsELNmwQJfv2Wd0215aUiKzZs62S9+bNm+sP4j1r6ahqISCcnJzEnj17hIqGSExMFB4eHmbh/681wloN8AMgPDw8RGJioip1EzIzM4Wvr69Z+Cdo5PNiloKrybGIrl27ivT09HYv/MLCQhEaGmoWfuatg23WQFdz/yAkJERkZWW1W+Hn5+eLvn37moV/BQiyVS87CLgMCH9/f5GUlNTuhJ+RkVG/p1sC9LX1OFMQxmWNwt3dvV055oSEhPo2/wrwkL1GjbsCCYBwdnZ2jEkcK2PDhg3Czc3NLPxUINDes4buwG5z/Dtx4kRRUlJyzwm+oqJCvPDCC/Xj/COmTqpDQAssMg27iqCgIHHs2LF7RvinT58WYWFhZsErpjldZxwQw83OWZZlMWXKFJsNZVur1S9YsEA4OzubhV8EjMPB4Wte3mLuL8TGxt5Vy1IURRHr168XAQEB9U3OTxg/HX/X4Gkgx1yBAQMGiK1btzo0EYqiiJ07d4ro6Oj6gr8ETOYuhQvwnxh3YgpA9O7dW6xcuVJUVVU5jOANBoPYuXOnGDBgQH3B1wD/ADy4B9ALWAVUmivYrVs38e6771pk2V9rcfbsWfHee++Jnj171hd8FfCxJYeSHQldTdHStfprLyMjI8WyZcvE6dOnrWqiFEURZ86cEcuXL68/hGBOpcDfAX9bCsRe29TdgQmmlQIj6q/O8PHxYdiwYQwfPpzo6GhCQ0PxbGSpSXNQVlZGRkYGCQkJxMXFcfDgQYqKGixMUDBuTtkIbAbKbC0IR/iWU4DJyf0ZGAzctmHLz8+PsLAwQkJC6NKlC+7u7nVJlmXKy8upqKigoqKC4uJicnJyOHfuHPn5+Y09T2/qvW8D/mUKm+0GycFMlBswBHjU1KeINGlLW6DDuAnxIMZtQYdNQYFDwNEIaAw9gFBTCgK8TJGJuylJJoFWmEzIddMcdoYp5ZlsvEPi/wHYeCTRqbrJ9gAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAxNy0wOC0wOVQxNDo0NTo1NSswMDowMNrkYDEAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMTctMDgtMDlUMTQ6NDU6NTUrMDA6MDCrudiNAAAAAElFTkSuQmCC">
                </div>
                <div class="vertical">
                    <div class="fontSize_13">Landeshauptstadt</div>
                    <div class="fontSize_13">München</div>
                    <div class="fontSize_13 bold">Kreisverwaltungsreferat</div>
                </div>
            </div>
            <div class="gridContainer_2_column_header_first">
                    <div></div>
                    <div class="vertical">
                            <div class="border border_bold padding bold">Briefwahlvorstand Nr.</div>
                            <div class="backendData noBorderTop border_bold padding">${data.wahlbezirkNummer}</div>
                    </div>
            </div>
            <div class="gridContainer_2_column_header_second marginTop_2"> 
                    <div class="fontSize_13 bold vertical">
                        <div>WAHLNIEDERSCHRIFT / Briefwahl (V1a)<br/>für die Wahl des Migrationsbeirates in der<br/>Landeshauptstadt München<br/>
                        <span class="fontSize_11">am ${data.wahltagFormatiert}</span></div>
                    </div>
                    <div>
                        <div class="fontSize_8 border padding">
                            Diese Wahlniederschrift ist unter Nr. 5.5.1 von allen Mitgliedern des Briefwahlvorstands zu unterschreiben.
                        </div>
                    </div>
            </div>
    `;
  }
  function _dataForChapterOneWahlvorstand(data: NiederschriftDruckInput) {
    return `
  <div class="fontSize_12 bold marginTop_2">Wahlhandlung</div>
            <div class="marginTop_1 marginBottom_5">
                Über den Ablauf der Wahl einschließlich der Ergebnisermittlung ist nachstehende Wahlniederschrift zu fertigen. Einzelheiten
                enthält die Wahlanweisung für Briefwahlvorstandsmitglieder. Im Zweifelsfall sind die Bestimmungen der Migrationsbeirateswahlordnung,
                der Migrationsbeiratssatzung, des Gemeinde- und Landkreiswahlgesetzes (GLKrWG) und der
                Gemeinde- und Landkreiswahlordnung (GLKrWO) maßgebend.    
            </div>

            <!-- 1. -->
            <!-- <div class="horizontal marginTopBottom_2 padding"> -->
    <div class="horizontal marginTopBottom_1">
                <div class="bold widthNumber">1.</div>
                <div class="bold">Briefwahlvorstand</div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>Zur Wahl des Migrationsbeirates waren vom Briefwahlvorstand erschienen:</div>
            </div>
            <div class="horizontal marginBottom_1">
                <table class="table marginTop widthCalc">
                    <colgroup>
                        <col width="4.59%"/>
                        <col width="34.24%"/>
                        <col width="36.17%"/>
                        <col width="25%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter" colspan="2">Familienname</td>
                        <td class="blueGrayBG textAlignCenter">Vorname</td>
                        <td class="blueGrayBG textAlignCenter">Funktion*</td>
                    </tr>
                    ${data.wahlvorstaende
                      .map((mitglied, idx) => {
                        return `<tr>
                        <td class="blueGrayBG" style="height: 0.9cm;">${idx + 1}.</td>
                        <td class="backendData">${mitglied && mitglied.nachname ? mitglied.nachname : ""}</td>
                        <td class="backendData">${mitglied && mitglied.vorname ? mitglied.vorname : ""}</td>
                        <td class="backendData">${mitglied && mitglied.funktionsName ? mitglied.funktionsName : ""}</td>
                        
                    </tr>
                    `;
                      })
                      .join("")}
                </table>
            </div>
            <div class="horizontal">
                <div class="horizontal">
                    <div class="bold">Hinweis:&nbsp;</div>
                    <div>Bei dem Begriff „Briefwahlvorstand“ ist das gesamte Gremium gemeint und nicht eine einzelne Person.</div>
                </div>
            </div>
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber"></div>
                <div>
                    Als Ersatz für nicht erschienene oder ausgefallene Mitglieder des Briefwahlvorstands hat die oder der Briefwahlvorsteher*
                    in folgende Personen zu Mitgliedern des Briefwahlvorstands ernannt:
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <table class="table width_100">
                    <colgroup>
                        <col width="4.47%"/>
                        <col width="34.24%"/>
                        <col width="21.3%"/>
                        <col width="20%"/>
                        <col width="20%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter" colspan="2">Familienname</td>
                        <td class="blueGrayBG textAlignCenter">Vorname</td>
                        <td class="blueGrayBG textAlignCenter">Funktion</td>
                        <td class="blueGrayBG textAlignCenter">Uhrzeit</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">1.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">2.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG">3.</td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                    </tr>
                </table>
            </div>
    `;
  }
  function _dataForChapterTwo(data: NiederschriftDruckInput) {
    return `
     <!-- 2. -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.</div>
                <div class="bold">Zulassung der Wahlbriefe</div>
            </div>

            <!-- 2.1 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.1</div>
                <div class="bold">Hinweis auf Verpflichtung des Briefwahlvorstands - Auflegung der Wahlvorschriften</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold"></div>
                <div>Der Briefwahlvorstand trat um<span class="backendData paddingLeftRight underline">${data.eroeffnungsuhrzeit.stunde} : ${data.eroeffnungsuhrzeit.minute}</span>Uhr zusammen.</div>
            </div>
            
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>
                    Die oder der Briefwahlvorsteher*in wies die übrigen Mitglieder des Briefwahlvorstands auf ihre Verpflichtung zur <b>unparteiischen Wahrnehmung</b> ihrer Aufgaben und zur <b>Verschwiegenheit</b> über die ihnen bei ihrer Tätigkeit bekannt gewordenen Angelegenheiten hin. Sie oder er belehrte sie über ihre Aufgaben.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Textausgaben der Migrationsbeiratswahlordnung, der Migrationsbeiratssatzung, des Gemeinde- und Landkreiswahlgesetzes
                    sowie der Gemeinde- und Landkreiswahlordnung waren im Auszählungsraum vorhanden.
                </div>
            </div>
            

            <!-- 2.2 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.2</div>
                <div class="bold">Wahlurne</div>
            </div>
            <div class="marginBottom_2 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Der Briefwahlvorstand stellte fest, dass sich die Wahlurne in ordnungsgemäßem Zustand befanden und leer waren. 
                    Sie wurde dann <b>versiegelt</b> und bis zur Entnahme der Stimmzettelumschläge nach Schluss der Wahlzeit
                    (18.00 Uhr) nicht mehr geöffnet.
                </div>
            </div>

            <!-- 2.3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.3</div>
                <div class="bold">Wahlbriefe und Verzeichnis der für ungültig erklärten Wahlscheine</div>
            </div>
            <div class="marginBottom_2 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Der Briefwahlvorstand stellte weiter fest, dass ihm <span class="backendData paddingLeftRight underline">${data.wahlbriefdaten.wahlbriefe}</span>Wahlbriefe,</br></br><span class="backendData paddingLeftRight underline">${data.wahlbriefdaten.verzeichnisseUngueltige}</span>Verzeichnis(se) der für ungültig erklärten Wahlscheine,</br></br><span class="backendData paddingLeftRight underline">${data.wahlbriefdaten.nachtraege}</span>Nachtrag/Nachträge zu diesem/n Verzeichnis(sen) übergeben worden waren.
                </div>
            </div>

            <!-- 2.4 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">2.4</div>
                <div class="bold">Öffnen und Prüfen der Wahlbriefe</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">2.4.1</div>
                <div>
                    Der Briefwahlvorstand öffnete die Wahlbriefe <b>einzeln und nacheinander</b>, er entnahm ihnen Wahlschein und Stimmzettelumschlag.                     
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Wenn der Wahlschein in einem Verzeichnis für ungültig erklärter Wahlscheine <u>nicht</u> aufgeführt war, der Wahlschein und 
                    der Stimmzettelumschlag eindeutig gültig waren und auch keinen Anlass zu Bedenken gaben, wurde der Stimmzettelumschlag <b>ungeöffnet</b> in die Wahlurne gelegt und der Wahlschein von einer oder einem Beisitzer*in gesammelt.
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Der nächste Wahlbrief wurde immer erst dann geöffnet, wenn der Briefwahlvorstand den vorhergehenden abschließend behandelt hatte. 
                </div>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber">2.4.2</div>
                <div class="vertical">
                    <div class="horizontal">
                        <div class="paddingLeft fontSize_14 backendDataColor marginCheckbox">${!data.wahlbriefdaten.nachtraeglichUeberbrachte || 0 === parseInt(data.wahlbriefdaten.nachtraeglichUeberbrachte) ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeft">
                            Es wurden keine weiteren Wahlbriefe überbracht.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="paddingLeft fontSize_14 backendDataColor marginCheckbox">${parseInt(data.wahlbriefdaten.nachtraeglichUeberbrachte) > 0 ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeft">
                            Ein*e Beauftragte*r der Stadt überbrachte die noch bis 18 Uhr eingegangen</br></br><span class="backendData paddingLeftRight underline">${(parseInt(data.wahlbriefdaten.nachtraeglichUeberbrachte) || 0) > 0 ? data.wahlbriefdaten.nachtraeglichUeberbrachte : "___"}</span>Wahlbriefe. Sie wurden entsprechend Nr. 2.4.1 behandelt.
                        </div>
                    </div>
                </div>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber">2.4.3</div>
                <div>
                    Die <b>Gesamtzahl</b> der zur Auswertung vorgelegten <b>Wahlbriefe</b> betrug<span class="underline paddingLeftRight">${(parseInt(data.wahlbriefdaten.wahlbriefe) || 0) + (parseInt(data.wahlbriefdaten.nachtraeglichUeberbrachte) || 0)}</span>.  
                </div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg>

            <!-- 2.5 -->
            <div class="horizontal marginBottom_5 paddingTopNewPage">
                <div class="widthNumber bold">2.5</div>
                <div class="bold">Zurückweisung von Wahlbriefen:</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">2.5.1</div>
                <div class="vertical">
                    <div class="horizontal">
                        <div class="backendDataColor fontSize_14 marginCheckbox">${data.beanstandeteWahlbriefe.gesamt === 0 ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeftRight">
                            Es wurden gegen keinen Wahlbrief Bedenken erhoben.
                        </div>
                    </div>
                    <div class="horizontal marginTop">
                        <div class="backendDataColor fontSize_14 marginCheckbox">${data.beanstandeteWahlbriefe.gesamt !== 0 ? "&#9746;" : "&#9744;"}</div>
                        <div class="paddingLeftRight">
                            Es wurden gegen insgesamt<span class="backendData paddingLeftRight underline">${data.beanstandeteWahlbriefe.gesamt !== 0 ? data.beanstandeteWahlbriefe.gesamt : ""}</span>Wahlbriefe Bedenken erhoben.
                        </div>
                    </div>
                </div>
            </div>

            <div class="horizontal marginBottom_5">
                    <div class="widthNumber">2.5.1.1</div>
                    <div>
                        Davon wurden durch Beschluss des Briefwahlvorstands <b>zurückgewiesen</b></br>
                        (→ Wahlbriefe nach Zurückweisungsgrund sortieren, nummerieren und anschließend in nachstehender
                        Tabelle erfassen)
                    </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <table class="table borderless">
                    <colgroup>
                        <col width="12.7%"/>
                        <col width="87%"/>
                    </colgroup>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.keinGueltigerWahlschein}</div>
                        </td>
                        <td>
                        Wahlbriefe, weil dem Wahlbriefumschlag kein oder kein gültiger Wahlschein (lt. Liste) beigefügt war,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.keineUnterschrift}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil die Versicherung an Eides statt nicht unterschrieben war,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.keinStimmzettelumschlag}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil dem Wahlbriefumschlag kein Stimmzettelumschlag beigefügt war,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.nichtVerschlossen}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil weder der Wahlbriefumschlag noch der Stimmzettelumschlag verschlossen war,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.mehrereStimmzettelumschlaege}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil der Wahlbriefumschlag mehrere Stimmzettelumschläge, aber nicht eine gleiche Anzahl gültiger und mit der vorgeschriebenen Versicherung an Eides statt versehener Wahlscheine enthielt,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.keinAmtlicherStimmzettelumschlag}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil kein amtlicher Stimmzettelumschlag benutzt worden war,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.loseStimmzettel}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil ein oder mehrere Stimmzettel außerhalb des Stimmzettelumschlags lagen,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.gegenstandImUmschlag + data.beanstandeteWahlbriefe.gefaehrdetWahlgeheimnis}</div>
                        </td>
                        <td>
                            Wahlbriefe, weil ein Stimmzettelumschlag benutzt worden war, der ein besonderes Merkmal aufwies oder einen deutlich fühlbaren Gegenstand enthielt,
                        </td>
                    </tr>
                    <tr>
                        <td style="height: 1.0cm;">
                            <div class="textRight border padding backendData">${data.beanstandeteWahlbriefe.gesamtMinusZugelassen}</div>
                        </td>
                        <td>
                            Wahlbriefe insgesamt.
                        </td>
                    </tr>
                </table>
            </div>

            <div class="horizontal marginBottom_5">
                    <div class="widthNumber">2.5.1.2</div>
                    <div>Davon wurden durch Beschluss des Briefwahlvorstands<span class="backendData paddingLeftRight underline">${data.beanstandeteWahlbriefe.zugelassen || data.beanstandeteWahlbriefe.zugelassen === 0 ? data.beanstandeteWahlbriefe.zugelassen : "    "}</span>Wahlbriefe<span class="bold paddingLeftRight">zugelassen</span>und entsprechend Nr. 3 behandelt. War Anlass der Beschlussfassung der Wahlschein, wurde der Wahlschein nummeriert und bei dem oder der Schriftführer*in gesondert verwahrt. Diese Wahlscheine wurden in die Wahlverhandlungstasche gelegt. 
                    </div>
            </div>

            <div class="horizontal marginBottom_5">
                    <div class="widthNumber">2.5.2</div>
                    <div>
                    Die<span class="bold paddingLeftRight">zurückgewiesenen</span>Wahlbriefe nach Nr. 2.5.1.1 wurden samt Inhalt (inkl. der Wahlscheine) ausgesondert,</br>
                    mit einem Vermerk über den Zurückweisungsgrund versehen,</br>
                    mit durchsichtigem Klebeband wieder verschlossen,</br>
                    in der Reihenfolge Ihrer Erfassung nummeriert,</br>
                    von einer oder einem Beisitzer*in in Verwahrung genommen und in die Wahlverhandlungstasche gelegt.
                    </div>
            </div>

            <div class="horizontal marginBottom_5">
                    <div class="widthNumber">2.5.3</div>
                    <div>
                        Die Einsender*innen <b>zurückgewiesener Wahlbriefe</b> wurden <b>nicht als Wähler*in gezählt;</b> ihre Stimmen gelten als nicht abgegeben.
                    </div>
            </div>

            <div class="horizontal marginBottom_5">
                    <div class="widthNumber bold">2.6</div>
                    <div>
                        Bevor mit der eigentlichen Auszählung begonnen wurde, wurde noch die Übergabe der Wahlbriefe abgewartet, 
                        die am Wahltag bis 18 Uhr bei der Stadt eingegangen sind. Diese Wahlbriefe wurden gemäß Nr. 2.4 behandelt.
                    </div>
            </div>

    `;
  }
  function _dataForChapterThree(data: NiederschriftDruckInput) {
    return `
    <!-- 3 -->
            <div class="horizontal marginBottom_5">
                <div class="widthNumber bold">3.</div>
                <div class="bold">Ermittlung und Feststellung des Briefwahlergebnisses</div>
            </div>

            <!-- 3.1 -->
            <div class="horizontal marginBottom_5">
                <div class="widthNumber bold">3.1</div>
                <div class="bold">Ermittlung der Zahl der Wähler*innen</div>
            </div>
            <div class="horizontal marginBottom_5">
                    <div class="widthNumber">3.1.1</div>
                    <div>
                        Nachdem alle rechtzeitig eingegangenen und nicht zurückgewiesenen Stimmzettelumschläge in die Wahlurne
                        gelegt wurden, öffnete die oder der Briefwahlvorsteher*in um
                        <span class="backendData paddingLeftRight underline">${data.schliessungsuhrzeit.stunde} : ${data.schliessungsuhrzeit.minute}</span>Uhr 
                        die Wahlurne und entnahm daraus die Stimmzettelumschläge. Der Briefwahlvorstand überzeugte sich, dass die Wahlurne vollständig leer war.
                    </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">3.1.2</div>
                <div class="vertical">
                    <div class="marginBottom_1">
                        Die Stimmzettelumschläge wurden ungeöffnet gezählt.
                    </div>
                    <div>
                        Die Zählung ergab<span class="backendData paddingLeftRight underline">${data.anzahlStimmzettel}</span>Stimmzettelumschläge (= <span class="border">B</span>).
                    </div>
                </div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg>
            <div class="horizontal marginBottom_5 paddingTopNewPage">
                <div class="widthNumber">3.1.3</div>
                <div class="vertical">
                    <div class="marginBottom_5">
                        Danach wurden die Wahlscheine der zugelassenen Wahlbriefe gezählt.
                    </div>
                    <div>
                        Die Zählung ergab<span class="backendData paddingLeftRight underline">${data.anzahlWahlscheine}</span> zugelassene Wahlscheine.
                    </div>
                </div>
            </div>

            <div class="horizontal marginBottom_5">
                <div class="widthNumber">3.1.4</div>
                <div>Kontrolle</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>
                    Die Anzahl der Stimmzettelumschläge (Nr. 3.1.2) 
                    stimmte mit der Anzahl der zugelassenen Wahlscheine (Nr. 3.1.3)
                </div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.anzahlWahlscheine === data.anzahlStimmzettel ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    überein
                </div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.anzahlWahlscheine !== data.anzahlStimmzettel ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    aus folgenden Gründen nicht überein:
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div class="backendData underline fontSize_7">
                    ${data.anzahlWahlscheine !== data.anzahlStimmzettel && data.begruendungStimmzettelumschlaege.grund ? data.begruendungStimmzettelumschlaege.grund : ""}
                </div>
            </div>

            <div class="horizontal marginBottom_5">
                <div class="widthNumber">3.1.5</div>
                <div>
                    Die oder der Schriftführer*in übertrug die Zahl der Wähler*innen in den Abschnitt 4.1 Kennbuchstabe <span class="border">B</span>.
                </div>
            </div>

            

            <!-- 3.2 -->
            <div class="horizontal marginBottom_5">
                <div class="widthNumber bold">3.2</div>
                <div class="bold">Öffnen der Stimmzettelumschläge, Entnahme der Stimmzettel</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">3.2.1</div>
                <div>
                    Die Stimmzettelumschläge wurden geöffnet und die Stimmzettel für die Migrationsbeiratswahl entnommen. Enthielt
                    ein Stimmzettelumschlag mehrere Stimmzettel , wurden diese Stimmzettel fest miteinander verbunden.
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">3.2.2</div>
                <div>
                    _____________ Stimmzettelumschläge enthielten keinen Stimmzettel für die Migrationsbeiratswahl, was auf dem
                    Stimmzettelumschlag vermerkt wurde. Diese leeren Stimmzettelumschläge wurden als 
                    <span class="bold redtext">
                        ungültige Stimmzettel = nicht gekennzeichnete Stimmzettel (Stapel Buchst. d) → rot)
                    </span> behandelt.
                </div>
            </div>

            <!-- 3.3 -->
            <div class="horizontal marginBottom_5 ">
                <div class="widthNumber bold">3.3</div>
                <div class="bold">Sortieren der Stimmzettel</div>
            </div>
            <div class="horizontal marginBottom_1">
                <div class="widthNumber"></div>
                <div>Die Stimmzettel wurden auf ihre Gültigkeit geprüft und auf folgende Stapeln sortiert:</div>
            </div>
            <div class="horizontal marginBottom_1 yellowBG" style="padding: 2px;">
                <div class="widthNumber yellowBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">a)</div>
                <div class="whiteBG" style="width: 100%;">
                    zweifelsfrei gültige Stimmzettel, auf denen <b>nur ein Wahlvorschlag</b> unverändert gekennzeichnet wurde
            (nur Kopfleistenkreuze), geordnet nach Wahlvorschlägen, &#8594; gelb
                </div>
            </div>
            <div class="horizontal marginBottom_1 greenBG" style="padding: 2px;">
                <div class="widthNumber greenBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">b)</div>
                <div class="whiteBG" style="width: 100%;">
                    zweifelsfrei gültige Stimmzettel, die <b>innerhalb nur eines Wahlvorschlags</b> verändert gekennzeichnet wurden
            (Einzelstimmvergabe mit und ohne Kopfleistenkreuz), geordnet nach Wahlvorschlägen, &#8594; hellgrün
                </div>
            </div>
            <div class="horizontal marginBottom_1 green_3_BG" style="padding: 2px;">
                <div class="widthNumber green_3_BG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">c)</div>
                <div class="whiteBG" style="width: 100%;">
                        zweifelsfrei gültige Stimmzettel, auf denen <b>verschiedene Wahlvorschläge</b> verändert gekennzeichnet wurden
            (Einzelstimmvergabe mit und ohne Kopfleistenkreuz), &#8594; dunkelgrün
                </div>
            </div>
            <div class="horizontal marginBottom_1 redBG" style="padding: 2px;">
                <div class="widthNumber redBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">d)</div>
                <div class="whiteBG" style="width: 100%;" >
                    nicht gekennzeichnete (leere) Stimmzettel sowie Stimmzettelumschläge, die keinen Stimmzettel enthielten &#8594; rot
                </div>
            </div>
            <div class="horizontal marginBottom_5 purpurBG" style="padding: 2px;">
                <div class="widthNumber purpurBG"></div>
                <div class="whiteBG" style="padding-left: 4px; padding-right: 8px;">e)</div>
                <div class="whiteBG" style="width: 100%;">
                    gekennzeichnete Stimmzettel (mit Anlass zu Bedenken), über die später vom Briefwahlvorstand
                    Beschluss zu fassen war (auch verbundene Stimmzettel aus demselben Stimmzettelumschlag) &#8594; lila
                </div>
            </div>

            <!-- 3.4 -->
            <div class="horizontal marginBottom_5 paddingTopNewPage">
                <div class="widthNumber bold">3.4</div>
                <div class="bold">Bildung von Arbeitsgruppen</div>
            </div>
            <div class="marginBottom_5 horizontal">
                <div class="widthNumber"></div>
                <div>
                    Es wurden Arbeitsgruppen mit je zwei Personen gebildet. Sie bekamen die Zähllisten für bestimmte Wahlvorschläge.
                    Die Namen der Personen der jeweiligen Arbeitsgruppe wurden auf den Zähllisten notiert. 
                </div>
            </div>
            
            <!-- 3.5 -->
            <div class="gridContainer_2_column_miniright marginBottom_5">
                <div>
                    <div class="horizontal">
                        <div class="widthNumber bold">3.5</div>
                        <div class="bold">Behandlung der Stimmzettel, die Anlass zu Bedenken gaben</div>
                    </div>
                    <div class="horizontal marginBottom_5">
                        <div class="widthNumber"></div>
                        <div><span class="bold lilatext">(Stapel gemäß 3.3 Buchst. e) &#8594; lila</span></div>
                    </div>
                    <div class="horizontal marginBottom_5">
                        <div class="widthNumber">3.5.1</div>
                        <div>
                            Die oder der Briefwahlvorsteher*in zeigte jeden einzelnen Stimmzettel den Mitgliedern des Briefwahlvorstands
                            und ließ über die Gültigkeit Beschluss fassen. Der Beschluss wurde auf der Rückseite des jeweiligen
                            Stimmzettels mit Unterschrift vermerkt. Dabei wurde auch der Grund angegeben, warum eine Stimmvergabe
                            für ungültig oder für gültig erklärt wurde.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_5">
                        <div class="widthNumber">3.5.2</div>
                        <div>
                            Die für <b>gültig</b> erklärten Stimmzettel wurden <b>gesondert</b> auf einem Stapel gesammelt. Sie werden mit den anderen gültigen Stimmzettel der Stapel (siehe Stapel gemäß 3.3 Buchst. <span class="yellowBG">a)</span>, <span class="greenBG">b)</span> oder <span class="green_3_BG">c)</span> ausgewertet. Sie dürfen nicht mit diesen Stapeln vermischt werden.
                        </div>
                    </div>
                    <div class="horizontal ">
                        <div class="widthNumber">3.5.3</div>
                        <div>
                            Die für <b>ungültig</b> erklärten Stimmzettel wurden <b>gesondert</b> gesammelt und nicht mit dem Stapel <span class="redBG">d)</span> vermischt. Nach Beschlussfassung und Zählung der (ungültigen) Stimmzettel wurden diese sofort in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                </div>
                <div class="purpurBG"></div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg></div>

            <!-- 3.6 -->
            <div class="gridContainer_2_column_miniright marginBottom_5 paddingTopNewPage">
                <div>
                    <div class="horizontal">
                        <div class="widthNumber bold">3.6</div>
                        <div class="bold">Behandlung der nicht gekennzeichneten Stimmzettel und der Stimmzettelumschläge, die keinen Stimmzettel enthielten. </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div><span class="bold redtext">(Stapel gemäß 3.3 Buchst. d &#8594; rot)</span></div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Die oder der Briefwahlvorsteher*in prüfte zuerst den Stapel mit den nicht gekennzeichneten Stimmzetteln
                            und die Stimmzettelumschläge, die keinen Stimmzettel enthielten. Sie oder er sagte jeweils an, dass die
                            Stimmvergabe ungültig ist.
                        </div>
                    </div>
                </div>
                <div class="redBG"></div>
            </div>

            <!-- 3.7 -->
            <div class="horizontal marginBottom_5">
                <div class="widthNumber bold">3.7</div>
                <div class="bold">Ermittlung der Zahl der ungültigen Stimmzettel</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Zwei Mitglieder des Briefwahlvorstands zählten unabhängig voneinander die nicht gekennzeichneten Stimmzettel,
                    die Stimmzettelumschläge ohne Stimmzettel und die durch Beschluss für ungültig erklärten Stimmzettel.
                 </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Die Zahl der ungültigen Stimmzettel und Stimmzettelumschläge wurde in Abschnitt 4.2 bei Kennbuchstabe <span class="border">C</span> eingetragen.</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>Die durch Beschluss für ungültig erklärten Stimmzettel (Nr. 3.5.3) wurden dann sofort in die Wahlverhandlungstasche gelegt.</div>
            </div>
            <!-- 3.8 -->
            <div class="gridContainer_2_column_miniright marginBottom_5">
                <div>
                    <div class="horizontal marginBottom_5">
                        <div class="widthNumber bold">3.8</div>
                        <div class="bold">Behandlung der Stimmzettel, auf denen nur ein Wahlvorschlag unverändert gekennzeichnet wurde <span class="yellowBG">(Stapel gemäß 3.3 Buchst. a – nur Kopfleistenkreuz) &#8594; gelb</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div class="bold">Bildung von Arbeitsgruppen</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Es wurden Arbeitsgruppen nach Wahlvorschlägen mit je zwei Personen gebildet. Die Namen der Personen
                            der jeweiligen Arbeitsgruppe wurden auf den Zähllisten erfasst.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>einziger Schritt: [Zählung der <b>Stimmzettel</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Zwei Mitglieder der für die Wahlvorschläge jeweils zuständigen Arbeitsgruppe zählten 
                            unabhängig voneinander die Stimm<b>zettel</b> des der Arbeitsgruppe zugeteilten 
                            Wahlvorschlags. Stimmte das Ergebnis der beiden Zählvorgänge nicht überein, wurde die Zählung wiederholt. 
                            Bei allen Zählungen wurde darauf geachtet, dass die Stimmzettel nach den Wahlvorschlägen richtig sortiert waren. 
                            Das Ergebnis wurde für jeden Wahlvorschlag in Abschnitt 4.2 bei Kennbuchstabe <span class="border">D1</span> usw. 
                            jeweils in Spalte 3 sowie in Abschnitt 4.3 bei Kennbuchstabe <span class="border">F</span> unter lfd. Nr. 
                            <span class="border">100</span> 
                            usw. eingetragen.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für gültig erklärten Stimmzettel (Nr. 3.5.2) mit nur einem unverändert gekennzeichneten 
                            Wahlvorschlag wurden gesondert verwahrt. Sie wurden ebenfalls gezählt zur Summe der gültigen Stimmzettel 
                            mit Kopfleistenkreuz nach Wahlvorschlag addiert und nach der Auswertung in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            &#8594; Nach der Erfassung dieser Stimmzettel wurden diese unverzüglich weggepackt und zurück in die Urne
                            gelegt. Diese Stimmzettel werden nicht mehr über die Zähllisten erfasst.
                        </div>
                    </div>
                </div>
                <div class="yellowBG"></div>
            </div>

            <!-- 3.9 -->
            <div class="gridContainer_2_column_miniright marginBottom_5 paddingTopNewPage">
                <div>
                    <div class="horizontal marginBottom_5">
                        <div class="widthNumber bold">3.9</div>
                        <div class="bold">Behandlung der Stimmzettel, die innerhalb nur eines Wahlvorschlags verändert gekennzeichnet wurden <span class="greenBG">(Stapel gemäß 3.3 Buchst. b) &#8594; hellgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>Schritt 1: [Zählung der <b>Stimmzettel</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Zwei Mitglieder der für die Wahlvorschläge jeweils zuständigen Arbeitsgruppe zählten unabhängig voneinander
                            die Stimmzettel des der Arbeitsgruppe zugeteilten Wahlvorschlags. Stimmte das Ergebnis der beiden
                            Zählvorgänge nicht überein, wurde die Zählung wiederholt. Bei allen Zählungen wurde darauf geachtet,
                            dass die Stimmzettel nach den Wahlvorschlägen richtig sortiert waren. Das Ergebnis wurde für jeden Wahlvorschlag
                            in Abschnitt 4.2 bei Kennbuchstabe <span class="border">D1</span> usw. jeweils in Spalte 4 eingetragen.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für <b>gültig</b> erklärten Stimmzettel (Nr. 3.3.2) mit Kennzeichnung innerhalb nur eines
                            Wahlvorschlages wurden gesondert verwahrt. Sie wurden ebenfalls gezählt zur Summe der gültigen Stimmzettel
                            mit Kennzeichnung nur innerhalb eines Wahlvorschlages addiert. Diese wurden getrennt von den anderen
                            Stapeln aufbewahrt, da sie nach der vollständigen Auswertung in die Wahlverhandlungstasche der
                            gelegt werden müssen.
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Alle Stimmzettel von <span class="greenBG">Stapel b) &#8594; hellgrün</span> (mit und ohne Beschluss) müssen später noch ausgewertet werden,
                            indem die Einzelstimmvergaben erfasst werden. Die Stapel der Wahlvorschläge sind daher so zu legen,
                            dass sie später nicht neu sortiert werden müssen.
                        </div>
                    </div>
                </div>
                <div class="greenBG"></div>
            </div>

            <!-- 3.10 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.10</div>
                <div class="bold">Schnellmeldung</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Für die Schnellmeldung wurden die Ergebnisse aus Abschnitt 4.2 Spalte 3 und 4 
                    in <b>Spalte 5</b> zusammengezählt und in den hierfür vorgesehenen Wahlvordruck V 3 übertragen 
                    und mit der Wahlverhandlungstasche abgegeben.
                </div>
            </div>

            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg>
            <!-- 3.11 -->
            <div class="gridContainer_2_column_miniright marginBottom_5">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.11</div>
                        <div class="bold">Behandlung der Stimmzettel, die innerhalb nur eines Wahlvorschlags verändert gekennzeichnet wurden <span class="greenBG">(Stapel gemäß 3.3 Buchst. b) &#8594; hellgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>[Zählung der einzelnen <b>Stimmen</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die Arbeitsgruppen erfassen alle Stimmen für die ihnen zu geordneten Wahlvorschläge durch Abstreichen
                            der einzelnen Bewerber*innen in der Zählliste, indem die Stimmen für die einzelnen sich bewerbenden Personen
                            durch eine Person der Arbeitsgruppe einzeln verlesen und von der anderen Person der Arbeitsgruppe
                            sofort bei Verlesung in der Zählliste abgestrichen wird. Die Stimmenzahl wird dabei wiederholt.
                        </div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Die oder der Briefwahlvorsteher*in und die Stellvertretung überwachen die ordnungsgemäße Führung der
                            Zähllisten. Auf den Zähllisten werden die Namen der Personen der Arbeitsgruppe erfasst. Die Zähllisten sind
                            zu unterschreiben.
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>
                            Die durch Beschluss für gültig erklärten Stimmzettel (Nr. 3.5.2) wurden als erste bearbeitet und sofort nach
                            der Auswertung und Erfassung in den Zähllisten in die Wahlverhandlungstasche gelegt.
                        </div>
                    </div>
                </div>
                <div class="greenBG"></div>
            </div>

            <!-- 3.12 -->
            <div class="gridContainer_2_column_miniright marginBottom_5">
                <div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber bold">3.12</div>
                        <div class="bold">Behandlung der Stimmzettel, auf denen verschiedene Wahlvorschläge verändert gekennzeichnet wurden <span class="green_3_BG">(Stapel gemäß 3.3 Buchst. c) &#8594; dunkelgrün</span></div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>[Zählung der einzelnen <b>Stimmen</b>]</div>
                    </div>
                    <div class="horizontal marginBottom_2">
                        <div class="widthNumber"></div>
                        <div>
                            Wurden verschiedene Wahlvorschläge gekennzeichnet, erfasst zunächst die erste Arbeitsgruppe die Stimmen
                            wie unter 3.11 beschrieben, anschließend wird auf dem Stimmzettel vermerkt, für welchen Wahlvorschlag
                            er ausgewertet wurde (Abhaken im Listenkopf mit dem violetten Zählstift). Dann wird er an die nächste
                            Arbeitsgruppe zur Auswertung weitergegeben.
                        </div>
                    </div>
                    <div class="horizontal">
                        <div class="widthNumber"></div>
                        <div>Die durch Beschluss für gültig erklärten Stimmzettel (Nr. 3.5.2) wurden als erste bearbeitet und sofort nach der Auswertung und Erfassung in den Zähllisten in die Wahlverhandlungstasche gelegt.</div>
                    </div>
                </div>
                <div class="green_3_BG"></div>
            </div>

            <!-- 3.13 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.13</div>
                <div class="bold">Bildung der Gesamtsumme aller Stimmen</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    In den Zähllisten wird für jede einzelne sich bewerbende Person die Gesamtzahl der abgestrichenen Stimmen
                    eingetragen. Diese Ergebnisse werden in Abschnitt 4.3 bei Kennbuchstabe F bei den einzelnen sich bewerbenden
                    Personen der jeweiligen Wahlvorschläge eingetragen. Anschließend wird die Gesamtstimmenzahl der
                    auf die einzelnen Wahlvorschläge insgesamt entfallenen gültigen Stimmen durch Zusammenzählen der für die
                    einzelnen Personen abgegebenen gültigen Stimmen ermittelt. Die so ermittelte Gesamtzahl wird in Abschnitt
                    4.2 bei Kennbuchstaben <span class="border">D1</span> usw. in Spalte 6 eingetragen.
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>Die Zähllisten sind von der oder dem Briefwahlvorsteher*in und von der erfassenden Person zu unterschreiben.</div>
            </div>

            <!-- 3.14 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">3.14</div>
                <div class="bold">Feststellung des Ergebnisses im Briefwahlstimmbezirk</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>
                    Das in Abschnitt 4 enthaltene Ergebnis wurde vom Briefwahlvorstand als das Ergebnis des Stimmbezirks festgestellt
                    und von der oder dem Briefwahlvorsteher*in im Auszählraum verkündet.
                </div>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>
    `;
  }
  function _dataForChapterFour(data: NiederschriftDruckInput) {
    return `
<!-- 4. -->
            
            <div class="marginBottom_1 gridContainer_2_column_smallright paddingTopNewPage">
                <div>
                    <img class="barcode"
                        src="${data.barcode}"
                        alt="">
                </div>
                <div class="backendData" style="direction:rtl">${data.wahlbezirkNummer}</div>
            </div>
            
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                    <div class="widthNumber bold">4.</div>
                    <div class="bold">Wahlergebnis</div>
            </div>

            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">4.1</div>
                <div class="bold">WÄHLER*INNEN (s. 3.1)</div>
            </div>
            <div class="horizontal marginBottom_2">
                <table class="table width_100">
                    <colgroup>
                        <col width="11.0%"/>
                        <col width="72.3%"/>
                        <col width="16.7%"/>
                    </colgroup>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">Kennbuchstabe</td>
                        <td class="blueGrayBG textAlignCenter">Bezeichnung</td>
                        <td class="blueGrayBG textAlignCenter">Anzahl</td>
                    </tr>
                    <tr>
                        <td class="blueGrayBG textAlignCenter">B</td>
                        <td>Wähler*innen</td>
                        <td class="border border_bold backendData textAlignRight">${data.bWerte}</td>
                    </tr>
                </table>
            </div>

            <div class="horizontal marginBottom_2 marginTop_5">
                <div class="widthNumber bold">4.2</div>
                <div class="bold">STIMMEN (s. 3.2 bis 3.13)</div>
            </div>
            
            <table class="table uncollapsed width_100 marginBottom_2">
                <colgroup>
                    <col width="10.5%"/>
                    <col width="38.2%"/>
                    <col width="12%"/>
                    <col width="12.65%"/>
                    <col width="12.65%"/>
                    <col width="14%"/>
                </colgroup>
                <tr>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Kennbuchstabe</br>(Ordnungszahl)</th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Name des Wahlvorschlags (Kennwort)</th>
                    <th colspan="3" class="blueGrayBG textAlignCenter noPadding">Gültige Stimm<b>zettel</b></th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter green_3_border border_bold noPadding">Gültige</br><b>kumulierte</b> und </br><b>panaschierte Stimmen</b></br><b>insgesamt</b></br>(keine unveränderten)</th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter  yellow_border border_bold noPadding" style="vertical-align: top;" >ein Wahlvorschlag</br><b>unverändert</b> gekennzeichnet</th>
                    <th class="blueGrayBG textAlignCenter  green_border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Innerhalb <b>eines</b> Wahlvorschlags <b>verändert</b></th>
                    <th class="blueGrayBG textAlignCenter  border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Gültige Stimmzettel für genau <b>einen</b> Wahlvorschlag <b>(Spalte 3 + Spalte 4)</b></th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter">1</th>
                    <th class="blueGrayBG textAlignCenter">2</th>
                    <th class="blueGrayBG textAlignCenter yellow_border_noTop border_bold">3</th>
                    <th class="blueGrayBG textAlignCenter green_border_noTop border_bold">4</th>
                    <th class="blueGrayBG textAlignCenter border_noTop border_bold">5</th>
                    <th class="blueGrayBG textAlignCenter green_3_border_noTop border_bold">6</th>
                </tr>
                ${data.gueltigeStimmenListe
                  .map((gueltigeStimmen) => {
                    return `<tr>
                    <td class="blueGrayBG textAlignCenter">D ${gueltigeStimmen.ordnungszahl}</td>
                    <td class="backendData">${gueltigeStimmen.parteiname}</td>
                    <td class="yellow_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelA}</td>
                    <td class="green_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelB}</td>
                    <td class="border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.gesamt}</td>
                    <td class="green_3_border_noTop border_bold backendData textAlignRight">${gueltigeStimmen.stapelBC}</td>
                    
                </tr>
                ${
                  gueltigeStimmen.ordnungszahl <
                    data.gueltigeStimmenListe.length &&
                  (gueltigeStimmen.ordnungszahl === 15 ||
                    (gueltigeStimmen.ordnungszahl > 15 &&
                      (gueltigeStimmen.ordnungszahl - 15) % 20 === 0))
                    ? `
                </table>
                <svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                <table class="table uncollapsed width_100 paddingTopNewPage marginBottom_2">
                <colgroup>
                    <col width="10.5%"/>
                    <col width="38.2%"/>
                    <col width="12%"/>
                    <col width="12.65%"/>
                    <col width="12.65%"/>
                    <col width="14%"/>
                </colgroup>
                <tr>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Kennbuchstabe</br>(Ordnungszahl)</th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter noPadding">Name des Wahlvorschlagträgers (Kennwort)</th>
                    <th colspan="3" class="blueGrayBG textAlignCenter noPadding">Gültige Stimm<b>zettel</b></th>
                    <th rowspan="2" class="blueGrayBG textAlignCenter green_3_border border_bold noPadding">Gültige</br><b>kumulierte</b> und </br><b>panaschierte Stimmen</b></br><b>insgesamt</b></br>(keine unveränderten)</th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter  yellow_border border_bold noPadding" style="vertical-align: top;" >ein Wahlvorschlag</br><b>unverändert</b> gekennzeichnet</th>
                    <th class="blueGrayBG textAlignCenter  green_border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Innerhalb <b>eines</b> Wahlvorschlags <b>verändert</b></th>
                    <th class="blueGrayBG textAlignCenter  border border_bold" style="vertical-align: top; padding: 0cm 0.1cm;">Gültige Stimmzettel für genau <b>einen</b> Wahlvorschlag <b>(Spalte 3 + Spalte 4)</b></th>
                </tr>
                <tr>
                    <th class="blueGrayBG textAlignCenter">1</th>
                    <th class="blueGrayBG textAlignCenter">2</th>
                    <th class="blueGrayBG textAlignCenter yellow_border_noTop border_bold">3</th>
                    <th class="blueGrayBG textAlignCenter green_border_noTop border_bold">4</th>
                    <th class="blueGrayBG textAlignCenter border_noTop border_bold">5</th>
                    <th class="blueGrayBG textAlignCenter green_3_border_noTop border_bold">6</th>
                </tr>
                `
                    : ""
                }
                `;
                  })
                  .join("")}
                <tr>
                    <td class="blueGrayBG textAlignCenter"><span class="bold">D</span></td>
                    <td>
                        <div class="horizontal spaceBetween">
                            <div>
                                <span class="bold">Gültige</span> insgesamt
                            </div>
                            <div class="fontSize_7 alignSelfEnd paddingLeftRight">
                                (Summe aus D${data.gueltigeStimmenListe[0].ordnungszahl} bis
                                D${data.gueltigeStimmenListe[data.gueltigeStimmenListe.length - 1].ordnungszahl})
                            </div>
                        </div>
                    </td>
                    <td class="yellow_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelA}</td>
                    <td class="green_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelB}</td>
                    <td class="border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.gesamt}</td>
                    <td class="green_3_border_noTop border_bold backendData textAlignRight">${data.gueltigeStimmenErgebnisGesamt.stapelBC}</td>
                </tr>
            </table>
            
            <div class="horizontal marginBottom_2">
                <table class="table" style="width: 86%">
                    <colgroup>
                            <col width="12.2%"/>
                            <col width="73.1%"/>
                            <col width="14.7%"/>
                    </colgroup>
                    <tr>
                        <td class="red_border border_bold blueGrayBG textAlignCenter">C</td>
                        <td class="red_border border_bold"><span class="bold">Ungültige</span> Stimm<span class="bold">zettel</span>
                        </td>
                        <td class="red_border border_bold backendData textAlignRight">${data.ungueltigeStimmen}</td>
                    </tr>
                </table>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill: #ffffff;" />
            </svg>

            <!-- 4.3 -->
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                <div class="widthNumber bold">4.3</div>
                <div class="bold">KANDIDAT*INNEN – Auswertung</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="horizontal widthNumber">
                    <div class="border blueGrayBG paddingLeftRight bold">F</div>
                    <div></div>
                </div>
                <div>Ergebnis der auf die einzelnen sich bewerbenden Personen entfallenen gültigen Stimmen (siehe Nr. 3.14)</div>
            </div>
            ${data.parteienListe
              .map((partei) => {
                return `<table class="table width_100 marginTop_5 marginBottom_2">
                    <colgroup>
                        <col width="25%"/>
                        <col width="25%"/>
                        <col width="25%"/>
                        <col width="25%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignCenter">Wahlvorschlag Nr.</th>
                        <th class="blueGrayBG textAlignCenter">${partei.ordnungszahl}</th>
                        <th class="blueGrayBG textAlignCenter">Kennwort</th>
                        <th class="textAlignCenter">${partei.kurzname}</th>
                    </tr>
                </table>
                <table class="table width_100 marginBottom_2">
                    <colgroup>
                        <col width="7.36%"/>
                        <col width="72%"/>
                        <col width="20.6%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignCenter">Lfd. Nr.</th>
                        <th rowspan="2" class="yellowBG textAlignCenter">Unveränderte Stimmzettel</br>(Die Stimmenzahl wurde aus Abschnitt 4.2 Kennbuchstabe D ${partei.ordnungszahl} Spalte 3 übertragen.)</th>
                        <th rowspan="2" class="textAlignCenter backendData">${
                          data.gueltigeStimmenListe.find((gueltigeStimme) => {
                            return (
                              gueltigeStimme.ordnungszahl ===
                              partei.ordnungszahl
                            );
                          }).stapelA
                        }</th>
                    </tr>
                    <tr>
                        <th class="blueGrayBG textAlignRight">${partei.direktKandMit00 ? partei.direktKandMit00.laufendeNr : parseInt(partei.ordnungszahl) * 100}</th>
                        
                    </tr>
                </table>
                <table class="table uncollapsed width_100 marginBottom_2">
                    <colgroup>
                    ${partei.maxcols
                      .map((col, idx) => {
                        return `<col width="${col.width1}%"/><col width="${col.width2}%"/>`;
                      })
                      .join("")}
                    </colgroup>
                    <tr>
                    ${partei.maxcols
                      .map((col, idx) => {
                        return `<th class="blueGrayBG textAlignCenter">Lfd. Nr.</th><th class="blueGrayBG textAlignCenter">Stimmen</th>`;
                      })
                      .join("")}
                    </tr>
                    ${partei._tabledata
                      .map((zeile, zeilenIndex) => {
                        return `<tr>
                            ${partei.maxcols
                              .map((col, idx) => {
                                const kand =
                                  idx < zeile.length && zeile[idx]
                                    ? zeile[idx]
                                    : { laufendeNr: "", ergebnis: "" };
                                kand.laufendeNr = "" + kand.laufendeNr;
                                kand.ergebnis =
                                  null !== kand.ergebnis
                                    ? "" + kand.ergebnis
                                    : "";
                                return `<td class="textAlignRight ${!kand.laufendeNr.length || !kand.ergebnis.length ? "blueGrayBG" : ""}">${kand.laufendeNr}</td><td class="textAlignRight ${!kand.ergebnis.length ? "blueGrayBG" : "backendData"}">${kand.ergebnis}</td>`;
                              })
                              .join("")}
                            </tr>
                            ${
                              (zeilenIndex + 1) % 31 === 0
                                ? `
                                </table>
                                <svg class="page_break" height="25px" width="100%">
                                    <rect width="1000" height="25" style="fill: #ffffff;" />
                                </svg>
                                <table class="table uncollapsed width_100 marginBottom_2 paddingTopNewPage">
                                    <colgroup>
                                    ${partei.maxcols
                                      .map((col, idx) => {
                                        return `<col width="${col.width1}%"/><col width="${col.width2}%"/>`;
                                      })
                                      .join("")}
                                    </colgroup>
                                    <tr>
                                    ${partei.maxcols
                                      .map((col, idx) => {
                                        return `<th class="blueGrayBG textAlignCenter">Lfd. Nr.</th><th class="blueGrayBG textAlignCenter">Stimmen</th>`;
                                      })
                                      .join("")}
                                    </tr>
                                `
                                : ""
                            }
                    `;
                      })
                      .join("")}
                    <tr class="noBorder">
                        ${partei.maxcols
                          .map((col, idx) => {
                            return `<th class="noBorder"></th><th class="noBorder"></th>`;
                          })
                          .join("")}
                    </tr>
                    <tr>
                        ${partei.maxcols
                          .map((col, idx) => {
                            return `<th class="textAlignCenter">Zus.</th><th class="textAlignRight backendData">${col.colsum}</th>`;
                          })
                          .join("")}
                    </tr>
                </table>
                
                <table class="table uncollapsed width_100 marginBottom_2">
                    <colgroup>
                        <col width="70%"/>
                        <col width="30%"/>
                    </colgroup>
                    <tr>
                        <th class="blueGrayBG textAlignRight bold">Gesamtsumme:</th>
                        <th class="green_3_border border_bold textAlignRight backendData">${
                          data.gueltigeStimmenListe.find((gueltigeStimme) => {
                            return (
                              gueltigeStimme.ordnungszahl ===
                              partei.ordnungszahl
                            );
                          }).stapelBC
                        }</th>
                    </tr>
                </table>
                <div class="marginBottom_2">Die Gesamtstimmenzahl wurde oben in den Abschnitt 4.2 Kennbuchstabe <b>D ${partei.ordnungszahl} in Spalte 6</b> übertragen.</div>
                <svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                `;
              })
              .join("")}
    `;
  }
  function _dataForChapterFive(data: NiederschriftDruckInput) {
    return `
    <!-- 5. -->
                
            <div class="horizontal marginBottom_5 paddingTopNewPage">
                <div class="widthNumber bold">5.</div>
                <div class="bold">Abschluss der Feststellung des Abstimmungsergebnisses</div>
            </div>

            <!-- 5.1 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.1</div>
                <div class="bold">Besondere Vorfälle</div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${!data.ereignisse || !data.ereignisse.hasEreignisse ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    Es ereigneten sich keine besonderen Vorfälle.
                </div>
            </div>
            <div class="horizontal">
                <div class="widthNumber"></div>
                <div class="backendDataColor fontSize_14 marginCheckbox">${data.ereignisse && data.ereignisse.hasEreignisse ? "&#9746;" : "&#9744;"}</div>
                <div class="paddingLeft">
                    Es ereigneten sich folgende besonderen Vorfälle ( &#8594; bei Bedarf eine Anlage beifügen):
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div class="width_100"></br><hr></br><hr></br><hr></div>
            </div>
            
            <!-- 5.2 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.2</div>
                <div class="bold">Anwesenheit des Briefwahlvorstands</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>
                    Während der Zulassung der Wahlbriefe sowie während der Ermittlung und der Feststellung des Wahlergebnisses
                    waren immer die Mitglieder aus dem Briefwahlvorstand mit der Funktion Briefwahlvorsteher*in und Schriftführer*
                    in oder deren Stellvertretung sowie mindestens ein*e Beisitzer*in anwesend.
                </div>
            </div>

            <!-- 5.3 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.3</div>
                <div class="bold">Öffentlichkeit der Wahlhandlung</div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>Die Zulassung der Wahlbriefe sowie die Ermittlung und die Feststellung des Wahlergebnisses waren öffentlich.</div>
            </div>
            <!-- 5.4 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.4</div>
                <div class="bold">Sofortige Übergabe der Unterlagen</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div>Nach Abschluss der Ergebnisermittlung und Einholung aller Unterschriften des Briefwahlvorstandes wurde sofort</div>
            </div>
            </br></br>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber"></div>
                <div><span class="bold" style="text-decoration:overline">(Name der Person aus dem Briefwahlvorstand eintragen)</span></div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <div>
                    mit dieser Niederschrift, und der Wahlverhandlungstasche (unverschlossen) zum Briefwahlservieceteam gebracht,
                    um die Unterlagen dort so schnell wie möglich abzugeben.
                </div>
            </div>           

            <!-- 5.5 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.5</div>
                <div class="bold">Unterschriften der Mitglieder des Briefwahlvorstands</div>
            </div>
            <div class="marginBottom_5 gridContainer_2_column_smallright">
                <div class="horizontal">
                    <div class="widthNumber">5.5.1</div>
                    <div>
                        Vorstehende Niederschrift wurde von allen Mitgliedern des Briefwahlvorstands durch ihre Unterschrift genehmigt.
                    </div>
                </div>
                <div class="border vertical negMarginTop">
                    <div class="padding">Unterschriften der Mitglieder des Briefwahlvorstands</div>
                </div>
            </div>
            <div class="marginBottom_5 marginTop_5 horizontal">
                <div class="widthNumber"></div>
                <table class="table borderless width_100">
                    <colgroup>
                        <col width="50%"/>
                        <col width="40%"/>
                        <col width="10%"/>
                    </colgroup>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Briefwahlvorsteher*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Stellvertretung Briefwahlvorsteher*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Schriftführer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Stellvertretung Schriftführer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                    <tr>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;">Beisitzer*in</td>
                        <td class="noPaddingTopBottom" style="height: 0.75cm;"><div class="onlyBottomBorder"></div></td>
                        <td class="noPaddingTopBottom" ></td>
                    </tr>
                </table>
            </div>
            
            <svg class="page_break" height="25px" width="100%">
                <rect width="1000" height="25" style="fill:#ffffff;" />
            </svg>
            <div class="horizontal marginBottom_2 paddingTopNewPage">
                    <div class="widthNumber">5.5.2</div>
                    <div>Folgende Mitglieder des Briefwahlvorstands verweigerten aus nachstehenden Gründen die Unterschrift:</div>
            </div>
            
            
            <div class="horizontal marginBottom_5">
                <div class="widthNumber"></div>
                <table class="table borderless width_100">
                    <colgroup>
                        <col width="10%"/>
                        <col width="40%"/>
                        <col width="10%"/>
                        <col width="40%"/>
                    </colgroup>
                    <tr>
                        <td style="height: 0.75cm; text-align: center;">Name</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                        <td style="height: 0.75cm; text-align: center;">Grund</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                    </tr>
                    <tr>
                        <td style="height: 0.75cm; text-align: center;">Name</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                        <td style="height: 0.75cm; text-align: center;">Grund</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                    </tr>
                    <tr>
                        <td style="height: 0.75cm; text-align: center;">Name</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                        <td style="height: 0.75cm; text-align: center;">Grund</td>
                        <td style="height: 0.75cm; text-align: left;"><div class="onlyBottomBorder"></div></td>
                    </tr>
                </table>
            </div>

            <!-- 5.6 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.6</div>
                <div class="bold">Ordnen und Verpacken der Wahlunterlagen</div>
            </div>
            <div class="marginBottom_2">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div>
                        Nach Feststellung des Wahlergebnisses wurden alle Stimmzettel, die nicht dieser Wahlniederschrift als Anlage beigefügt sind, wie folgt geordnet und verpackt:
                    </div>
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.6.1</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den nicht beschlussmäßig behandelten gültigen Stimmzetteln. Es wurden getrennt nach Wahlvorschlägen</br>
                    - die unveränderten Stimmzettel mit nur einem Kopfleistenkreuz <span class="yellowBG">(gelb)</span>,</br>
                    - die nur innerhalb eines Wahlvorschlages gekennzeichneten Stimmzettel <span class="greenBG">(hellgrün)</span>,</br>
                    verpackt.
                </div>
            </div>
             <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.6.2</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den gültigen Stimmzettel, auf denen verschiedene Wahlvorschläge verändert gekennzeichnet wurden <span class="green_3_text">(dunkelgrün)</span>,
                </div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.6.3</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    ein Paket mit den nicht gekennzeichneten Stimmzetteln und leeren Stimmzettelumschlägen (Stapel gemäß 3.3 → <span class="bold redtext">Stapel d</span>),
                </div>
            </div>
            
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">5.6.4</div>
                <div class="backendDataColor fontSize_14 marginCheckbox">&#9744;</div>
                <div class="paddingLeft">
                    die beschlussmäßig behandelten Stimmzettel (gültig und ungültig) wurden spätestens jetzt in die Wahlverhandlungstasche eingelegt (Stapel gemäß 3.3 → <span class="bold lilatext">Stapel e</span>),
                </div>
            </div>
            
            <!-- 5.7 -->
            <div class="horizontal marginBottom_2">
                <div class="widthNumber bold">5.7</div>
                <div class="bold">Übergabe der Wahlunterlagen – sofort nach Auszählung der Wahl!</div>
            </div>
            <div class="horizontal marginBottom_2">
                <div class="widthNumber">5.7.1</div>
                <div>
                    Dem Briefwahlserviceteam wurden <b>unverzüglich</b> nach Auszählung um ___________ Uhr, in der Wahlverhandlungstasche (nicht versiegelt) übergeben:
                </div>
            </div>
            <div class="vertical marginBottom_2">
                <div class="horizontal">
                    <div class="widthNumber"></div>
                    <div class="vertical">
                        <div>- diese Niederschrift,</div>
                        <div>- die beschlussmäßig behandelten Stimmzettel (Nr. 5.6.4),</div>
                        <div>- die Schnellmeldung V3,</div>
                        <div>- Zähllisten für alle Wahlvorschläge.</div>
                    </div>
                </div>
            </div>
            <div class="horizontal marginBottom_5">
                <div class="widthNumber">5.7.2</div>
                <div>
                    Die Stimmzettelpakete wie in Nr. 5.6 beschrieben wurden in die Urne gelegt und diese Urne wurde dann versiegelt.
                </div>
            </div>
            
            <div class="marginTop_5"></div>
            <table class="table borderless" style="margin: 0.6cm auto;">
                <tr>
                    <th style="width: 5.87cm; text-align: center;">Ordnungsgemäß übergeben</th>
                    <th class="marginLeft_15" style="width: 5.87cm; text-align: justify;">
                        Von der oder dem Beauftragten nach Prüfung auf Vollständigkeit übernommen:
                    </th>
                </tr>
                <tr>
                    <td style="height: 0.9cm;"></td>
                    <td style="height: 0.9cm;"></td>
                </tr>
                <tr>
                    <td style="min-height: 0.9cm;"> <div class="onlyBottomBorder border_bold marginTop_5"></div></td>
                    <td class="marginLeft_15" style="min-height: 0.9cm;"> <div class="onlyBottomBorder border_bold marginTop_5"></div></td>
                </tr>
                <tr>
                    <td> <div class="textAlignCenter" style="min-height: 0.55cm;">(Unterschrift)</div></td>
                    <td class="marginLeft_15"> <div class="textAlignCenter" style="min-height: 0.55cm;">(Unterschrift)</div></td>
                </tr>
            </table>
            
            ${
              data.ereignisse &&
              data.ereignisse.vorfaelle &&
              data.ereignisse.vorfaelle.length > 0
                ? `<svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                <br/><h1>Anlage V1a Vorfälle und Vorkommnisse</h1><br/>` +
                  data.ereignisse.vorfaelle
                    .map((vorfall, i) => {
                      let counter = i + 1;
                      return (
                        `<div>Um ` +
                        vorfall.uhrzeit +
                        ` ereignete sich folgendes Vorkommnis: Vk` +
                        counter +
                        ` ` +
                        vorfall.beschreibung +
                        `</div>`
                      );
                    })
                    .join("")
                : ""
            }

            ${
              data.ereignisse &&
              data.ereignisse.vorkommnisse &&
              data.ereignisse.vorkommnisse.length > 0
                ? `<svg class="page_break" height="25px" width="100%">
                    <rect width="1000" height="25" style="fill: #ffffff;" />
                </svg>
                <br/><h1>Anlage V1a Vorfälle und Vorkommnisse</h1><br/>` +
                  data.ereignisse.vorkommnisse
                    .map((vorkommnis, i) => {
                      let counter = i + data.ereignisse.vorfaelle.length + 1;
                      return (
                        `<div>Um ` +
                        vorkommnis.uhrzeit +
                        ` ereignete sich folgendes Vorkommnis: Vk` +
                        counter +
                        ` ` +
                        vorkommnis.beschreibung +
                        `</div>`
                      );
                    })
                    .join("")
                : ""
            }
                
      
    `;
  }
  function _getStyling() {
    return `
    <style type="text/css">
                    /****** Print Header ******/
                    @page {
                        size: A4;
                        margin-top: 0.0cm;
                        margin-left: 0;
                        margin-right: 0;
                    }

                    @media print {
                        div.footer {
                            padding-top: 0.1cm;
                            position: fixed;
                            bottom: 0;
                            left: 1cm;
                            font-size: x-small;
                            z-index: 0;
                        }
                    }

                    /****** Default Value Tags ******/
                    body {
                        max-width: 17cm;
                        margin: 0cm 2cm 0.2cm;
                        font-size: 9pt;
                        writing-mode: horizontal-tb;
                        font-family: Arial, serif;
                        -ms-hyphens: auto;
                        -webkit-hyphens: auto;
                        hyphens: auto;
                    }

                    /****** Table ******/
                    table {
                        border-collapse: collapse;
                    }

                    table.uncollapsed {
                        border-collapse: separate;
                        border-spacing: 0px;
                    }

                    table.borderless {
                        border-width: 0px;
                    }

                    table.borderless th {
                        border-width: 0px;
                    }

                    table.borderless td {
                        border-width: 0px ;
                    }

                    table.backandborder {
                        border: 1px solid #546e7a;
                    }

                    table.backandborder th {
                        border: 1px solid #546e7a;
                    }

                    table.backandborder td {
                        border: 1px solid #546e7a;
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

                    .linestable {
                        width: 80%;
                    }

                    .linestable th {
                        border-top: none;
                        border-left: none;
                        border-right: none;
                        height: 0.6cm;
                    }

                    .linestable td {
                        border-top: none;
                        border-left: none;
                        border-right: none;
                        height: 0.6cm;
                    }

                    hr { 
                        display: block;
                        margin-before: 0.5em;
                        margin-after: 0.5em;
                        margin-start: auto;
                        margin-end: auto;
                        overflow: hidden;
                        border-style: inset;
                        border-width: 1px;
                    }

                    /****** Borders ******/
                    .noBorder {
                        border-bottom: none;
                        border-left: none;
                        border-right: none;
                        border-top: none;
                    }


                    /****** Borders ******/
                    .noBorderTop {
                        border-bottom: 1px solid black;
                        border-left: 1px solid black;
                        border-right: 1px solid black;
                        border-top: none;
                    }

                    .border {
                        border: 1px solid #000000;
                    }

                    .border_noTop {
                        border: 1px solid #000000;
                        border-top: none;
                    }

                    .yellow_border {
                        border: 1px solid #ffff99;
                    }

                    .yellow_border_noTop {
                        border: 1px solid #ffff99;
                        border-top: none;
                    }

                    .green_border {
                        border: 1px solid #99ff66;
                    }

                    .green_border_noTop {
                        border: 1px solid #99ff66;
                        border-top: none;
                    }

                    .green_3_border {
                        border: 1px solid #29cc29;
                    }

                    .green_3_border_noTop {
                        border: 1px solid #29cc29;
                        border-top: none;
                    }

                    .red_border {
                        border: 1px solid #ff3333;
                    }

                    .purpur_border {
                        border: 1px solid #9900ff;
                    }
                    
                    .onlyBottomBorder {
                        border-bottom: 1px solid black;
                        border-left: none;
                        border-right: none;
                        border-top: none;
                    }

                    .border_bold {
                        border-width: 3px;
                    }

                    .borderTop_bold {
                        border-top-width: 3px;
                    }

                    .borderBottom_bold {
                        border-bottom-width: 3px;
                    }

                    .borderLeft_bold {
                        border-left-width: 3px;
                    }

                    .borderRight_bold {
                        border-right-width: 3px;
                    }

                    /****** Flex ******/
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

                    .flexCenter {
                        justify-content: center;
                    }

                    .flexEnd {
                        justify-content: flex-end;
                    }

                    .alignItemsCenter {
                        align-items: center;
                    }

                    .alignSelfCenter {
                        align-self: center;
                    }

                    .alignSelfEnd {
                        align-self: flex-end;
                    }

                    /****** Grid ******/
                    .gridContainer_2_column_miniright {
                        display: grid;
                        grid-template-columns: 16.60cm 0.25cm;
                        grid-column-gap: 0.15cm;
                    }

                    .gridContainer_2_column_smallright {
                        display: grid;
                        grid-template-columns: 10.5cm 6cm;
                        grid-column-gap: 0.5cm;
                    }

                    .gridContainer_3_column_header {
                        display: grid;
                        grid-template-columns: 7.5cm 3.85cm 6.25cm;
                        grid-column-gap: 0.3cm;
                        min-height: 3cm;
                    }

                    .gridContainer_2_column_header_first {
                        display: grid;
                        grid-template-columns: 10.5cm 5.25cm;
                        grid-column-gap: 1.25cm;
                        min-height: 1.5cm;
                    }

                    .gridContainer_2_column_header_second {
                        display: inline-grid;
                        grid-template-columns: 10.5cm 5.25cm;
                        grid-column-gap: 1.25cm;
                        min-height: 1.5cm;
                    }

                    /****** Text Alignment ******/
                    .textAlignCenter {
                        text-align: center;
                    }
                    .textAlignRight {
                        text-align: right;
                    }

                    /***** Font-Weight *****/
                    .bold {
                        font-weight: bold;
                    }

                    /****** Text-Decoration ******/
                    .underline {
                        text-decoration: underline;
                    }

                    /****** Font-Size ******/
                    .fontSize_7 {
                        font-size: 7pt;
                    }

                    .fontSize_8 {
                        font-size: 8pt;
                    }

                    .fontSize_10 {
                        font-size: 10pt;
                    }

                    .fontSize_11 {
                        font-size: 11pt;
                    }

                    .fontSize_12 {
                        font-size: 12pt;
                    }

                    .fontSize_13 {
                        font-size: 13pt;
                    }

                    .fontSize_14 {
                        font-size: 14pt;
                    }

                    /****** BackgroundColor ******/
                    .blueGrayBG {
                        background-color: #e6e6ff;
                    }

                    /****** Width ******/
                    .width_100 {
                        width: 100%;
                    }

                    .widthNumber {
                        width: 1.25cm;
                        min-width: 1.25cm;
                    }

                    .widthCalc {
                        width: calc(100% - 0.6cm);
                    }

                    /****** Padding ******/
                    .noPadding {
                        padding: 0;
                    }

                    .noPaddingTopBottom {
                        padding-top: 0;
                        padding-bottom: 0;
                    }

                    .paddingLeft {
                        padding-left: 0.2cm;
                    }

                    .paddingLeftCheckbox {
                        padding-left: 0.5cm;
                    }

                    .paddingTable {
                        padding: 6px 2px;
                    }

                    .paddingLeftRight {
                        padding: 0 0.2cm;
                    }

                    .paddingLeft_1 {
                        padding: 0.2cm 0 0.2cm 0.1cm;
                    }

                    .paddingTopBottom {
                        padding: 0.2cm 0;
                    }

                    .padding {
                        padding: 0.1cm;
                    }

                    .paddingHeader {
                        padding: 0.1cm 0.2cm;
                    }

                    .paddingTopNewPage {
                        padding-top: 0.2cm;
                    }

                    /****** Margin ******/
                    .marginLeft {
                        margin-left: 0.2cm;
                    }

                    .marginLeft_10 {
                        margin-left: 1cm;
                    }

                    .marginTopBottom_1 {
                        margin-top: 0.1cm;
                        margin-bottom: 0.1cm;
                    }

                    .marginTopBottom_2 {
                        margin-top: 0.2cm;
                        margin-bottom: 0.2cm;
                    }

                    .marginTopBottom_3 {
                        margin-top: 0.3cm;
                        margin-bottom: 0.3cm;
                    }

                    .marginTop_1 {
                        margin-top: 0.1cm;
                    }

                    .marginTop_1_15 {
                        margin-top: 1.15cm;
                    }

                    .marginTop_2 {
                        margin-top: 0.2cm;
                    }

                    .marginTop_5 {
                        margin-top: 0.5cm;
                    }

                    .marginBottom_1 {
                        margin-bottom: 0.1cm;
                    }

                    .marginBottom_1_5 {
                        margin-bottom: 0.15cm;
                    }

                    .marginBottom_2 {
                        margin-bottom: 0.2cm;
                    }

                    .marginBottom_5 {
                        margin-bottom: 0.5cm;
                    }

                    .marginBottom_6 {
                        margin-bottom: 0.6cm;
                    }

                    .marginCheckbox {
                        margin-top: -0.05cm;
                    }

                    .negMarginTop {
                        margin-top: -0.2cm;
                    }

                    /****** Barcode ******/
                    .barcode {
                        height: 2.4cm;
                        width: 7.5cm;
                    }

                    /****** Backend-Data ******/
                    .backendData {
                        font-weight: bold;
                        font-style: italic;
                        color: #546e7a;
                    }

                    .backendDataColor {
                        font-weight: bold;
                        color: #546e7a;
                    }

                    .whiteBG {
                        background-color: #ffffff !important;
                    }

                    .yellowBG {
                        background-color: #ffff99;
                        
                    }

                    .greenBG {
                        background-color: #99ff66;
                    }

                    .green_3_BG {
                        background-color: #29cc29;
                    }

                    .redBG {
                        background-color: #ff3333;
                    }

                    .purpurBG {
                        background-color: #9900ff;
                    }

                    .greentext {
                        color: #99ff66;
                    }

                    .green_3_text {
                        color: #29cc29;
                    }

                    .redtext {
                        color: #ff0000;
                    }

                    .lilatext {
                        color: #a37acc;
                    }

                    /****** Page Break ******/
                    .page_break {
                        page-break-before: always;
                    }
                </style>
    `;
  }

  function buildNiederschriftTemplateFromData(data: NiederschriftDruckInput) {
    return `
    <!DOCTYPE html>
            <html lang="de">
           ${_dataForHeader(data)}

        
            ${_dataForChapterOneWahlvorstand(data)}
            ${_dataForChapterTwo(data)}
            ${_dataForChapterThree(data)}
            ${_dataForChapterFour(data)}
            ${_dataForChapterFour(data)}  
            ${_dataForChapterFive(data)}
            </body>
            <div class="footer">${data.footer}</div>
            </html>
            

            
    `;
  }

  return {
    buildNiederschriftTemplateFromData,
  };
}
