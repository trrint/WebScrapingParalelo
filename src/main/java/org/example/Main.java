package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

    public static void main(String[] args) {
        // Lista de URLs que deseja verificar
        List<String> urls = new ArrayList<>();
        urls.add("https://www.uol.com.br/");
        urls.add("https://ge.globo.com/");

        // Palavra-chave que deseja procurar nas páginas da web
        String keyword = "Lula";

        // Tags HTML que deseja pesquisar
        List<String> tagsToSearch = Arrays.asList("h1", "p", "h2", "h3", "a");

        // Número de threads no pool de threads
        int numThreads = 15;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Future<String>> futures = new ArrayList<>();

        for (String url : urls) {
            Callable<String> webScrapingTask = new Callable<String>() {
                @Override
                public String call() throws Exception {
                    StringBuilder result = new StringBuilder();

                    try {
                        // Faz o request para a URL e obtém o HTML da página
                        Document document = Jsoup.connect(url).get();

                        // Itera sobre as tags específicas para busca
                        for (String tag : tagsToSearch) {
                            Elements elements = document.select(tag);

                            // Itera sobre os elementos da tag e verifica se a palavra-chave está presente
                            for (Element element : elements) {
                                String elementText = element.text();
                                if (elementText.contains(keyword)) {
                                    result.append(elementText).append("\n");
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return result.toString();
                }
            };

            futures.add(executorService.submit(webScrapingTask));
        }

        // Aguarda a conclusão de todas as tarefas e processa as saídas sincronizadamente
        for (Future<String> future : futures) {
            try {
                String output = future.get();
                System.out.println(output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Encerra o programa
        executorService.shutdown();
    }
}
