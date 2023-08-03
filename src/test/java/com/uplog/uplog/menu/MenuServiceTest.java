package com.uplog.uplog.menu;
import com.uplog.uplog.domain.menu.application.MenuService;
import com.uplog.uplog.domain.menu.dao.MenuRepository;
import com.uplog.uplog.domain.menu.dto.MenuDTO;
import com.uplog.uplog.domain.menu.model.Menu;
import com.uplog.uplog.domain.post.application.PostService;
import com.uplog.uplog.domain.post.dao.PostRepository;
import com.uplog.uplog.domain.project.dao.ProjectRepository;
import com.uplog.uplog.domain.project.dto.ProjectDTO;
import com.uplog.uplog.domain.project.model.Project;
import com.uplog.uplog.domain.task.application.TaskService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.uplog.uplog.domain.menu.dto.MenuDTO.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
public class MenuServiceTest {

}
